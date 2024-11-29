package com.br.tudodelicia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.io.File
import java.util.UUID

class CreateRecipe : AppCompatActivity() {

    private lateinit var ivRecipeImage: ImageView
    private lateinit var etRecipeName: EditText
    private lateinit var etRecipeDescription: EditText
    private lateinit var ingredientsContainer: LinearLayout
    private lateinit var stepsContainer: LinearLayout
    private lateinit var btnAddIngredient: Button
    private lateinit var btnAddStep: Button
    private lateinit var btnSubmitRecipe: Button
    private lateinit var ivRecipeImageButton: ImageButton

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private var stepsCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ivRecipeImage = findViewById(R.id.iv_recipe_image)
        ivRecipeImageButton = findViewById(R.id.iv_recipe_image_button)
        etRecipeName = findViewById(R.id.et_recipe_name)
        etRecipeDescription = findViewById(R.id.et_recipe_description)
        ingredientsContainer = findViewById(R.id.ingredients_container)
        stepsContainer = findViewById(R.id.steps_container)
        btnAddIngredient = findViewById(R.id.btn_add_ingredient)
        btnAddStep = findViewById(R.id.btn_add_step)
        btnSubmitRecipe = findViewById(R.id.btn_submit_recipe)

        ivRecipeImage.setOnClickListener { openImagePicker() }
        ivRecipeImageButton.setOnClickListener { openImagePicker() }
        btnAddIngredient.setOnClickListener { addIngredientField(ingredientsContainer) }
        btnAddStep.setOnClickListener { addStepToList(stepsContainer) }
        btnSubmitRecipe.setOnClickListener { submitRecipe() }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            ivRecipeImage.setImageURI(selectedImageUri)
        }
    }

    private fun addIngredientField(container: LinearLayout) {
        val ingredientView = layoutInflater.inflate(R.layout.item_ingredient, container, false)
        val editText = ingredientView.findViewById<EditText>(R.id.et_ingredient)
        val btnRemove = ingredientView.findViewById<ImageButton>(R.id.btn_remove)

        editText.hint = "Digite o ingrediente"
        btnRemove.setOnClickListener { ingredientsContainer.removeView(ingredientView) }
        container.addView(ingredientView)
    }

    private fun handleRemoveStep(container: LinearLayout, stepView: View) {
        ingredientsContainer.removeView(stepView)
        stepsCounter = 0
        for (count in 0 until container.childCount) {
            val tempStepView = container.getChildAt(count)
            stepsCounter += 1
            tempStepView.findViewById<TextView>(R.id.tv_step_label).text = "Passo ${stepsCounter}:"
        }
    }

    private fun addStepToList(container: LinearLayout) {
        stepsCounter += 1
        val stepView = layoutInflater.inflate(R.layout.item_step, container, false)
        val editText = stepView.findViewById<EditText>(R.id.et_step)
        val stepsLabel = stepView.findViewById<TextView>(R.id.tv_step_label)
        val btnRemove = stepView.findViewById<ImageButton>(R.id.btn_remove)

        stepsLabel.text = "Passo ${stepsCounter}:"
        editText.hint = "Digite o passo"
        btnRemove.setOnClickListener { handleRemoveStep(container, stepView) }
        container.addView(stepView)

    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun uriToFile( uri: Uri): File? {
        val contentResolver = this.contentResolver
        val tempFile = File.createTempFile("temp_image", ".jpg", this.cacheDir) // Cria um arquivo temporário
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    private fun imageFileToBase64(filePath: Uri): String? {
        val file = uriToFile(filePath)
        if (!file?.exists()!!) return null
        val bitmap = BitmapFactory.decodeFile(file.path)
        return bitmapToBase64(bitmap)
    }

    private fun submitRecipe() {
        val recipeName = etRecipeName.text.toString()
        val recipeDescrition = etRecipeDescription.text.toString()
        if (recipeName.isBlank() || recipeDescrition.isBlank() || selectedImageUri == null) {
            Toast.makeText(this, "Preencha todos os campos e adicione uma imagem!", Toast.LENGTH_SHORT).show()
            return
        }

        val imgBase64 = imageFileToBase64(selectedImageUri!!)
        val ingredients = mutableListOf<String>()
        for (i in 0 until ingredientsContainer.childCount) {
            val ingredientView = ingredientsContainer.getChildAt(i)
            val etIngredient = ingredientView.findViewById<TextView>(R.id.et_ingredient)
            ingredients.add(etIngredient.text.toString())
        }

        val steps = mutableListOf<String>()
        for (i in 0 until stepsContainer.childCount) {
            val stepView = stepsContainer.getChildAt(i)
            val etStep = stepView.findViewById<TextView>(R.id.et_step)
            steps.add(etStep.text.toString())
        }

        val recipe = mapOf(
            "title" to recipeName,
            "description" to recipeDescrition,
            "img" to imgBase64,
            "ingredients" to ingredients,
            "steps" to steps,
            "createdAt" to System.currentTimeMillis()
        )

        val db = Firebase.firestore
        db.collection("recipes")
            .add(recipe)
            .addOnSuccessListener {
                Toast.makeText(this, "Receita enviada com sucesso!", Toast.LENGTH_SHORT).show()
                etRecipeName.text.clear()
                selectedImageUri = null
                ingredientsContainer.removeAllViews()
                stepsContainer.removeAllViews()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                println(e.message)
                Toast.makeText(this, "Erro ao enviar a receita: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        Toast.makeText(this, "Receita enviada com sucesso!", Toast.LENGTH_SHORT).show()
    }
}