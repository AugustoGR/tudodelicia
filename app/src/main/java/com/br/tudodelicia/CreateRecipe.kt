package com.br.tudodelicia

import android.content.Context
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
import java.io.File

class CreateRecipe : AppCompatActivity() {

    private lateinit var ivRecipeImage: ImageView
    private lateinit var etRecipeName: EditText
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

        ivRecipeImage = findViewById(R.id.iv_recipe_image)
        ivRecipeImageButton = findViewById(R.id.iv_recipe_image_button)
        etRecipeName = findViewById(R.id.et_recipe_name)
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun getFileExtension( uri: Uri): String? {
        return this.contentResolver.getType(uri)?.let { mimeType ->
            android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }
    }

    private fun imageFileToBase64(filePath: Uri): String? {
        val ext = getFileExtension(filePath)
        val pathName = "${filePath.path}.${ext}"
        val file = File(pathName)
        println(file)
        println(file)
        if (!file.exists()) return null
        val bitmap = BitmapFactory.decodeFile(pathName)
        return bitmapToBase64(bitmap)
    }

    private fun submitRecipe() {
        val recipeName = etRecipeName.text.toString()
        if (recipeName.isBlank() || selectedImageUri == null) {
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

        println(recipeName)
        println(ingredients)
        println(steps)
        println(imgBase64)

        Toast.makeText(this, "Receita enviada com sucesso!", Toast.LENGTH_SHORT).show()
    }
}