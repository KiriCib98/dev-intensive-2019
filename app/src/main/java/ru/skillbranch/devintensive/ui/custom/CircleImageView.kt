package ru.skillbranch.devintensive.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.Dimension
import androidx.core.content.res.ResourcesCompat
import ru.skillbranch.devintensive.R


// @JvmOverloads при компиляции будет создано 3 конструктора, для каждого из возможных аргументов
class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private var cvBorderColor = Color.WHITE
    private var cvBorderWidth = 2

    private var cvBitmapDiameter = 120f// Image diameter

    private var cvBitmap: Bitmap? = null // исходная картинка
    private var cvBitmapPaint = Paint()  // для отрисовки картинки
    private var cvBorderPaint = Paint()  // для отрисовки границ
    private var cvBitmapShader: BitmapShader? = null
    private var widthOrHeight = 0f   // Control width
    private var widthSpecSize = 0    // Control initial setting width
    private var heightSpecSize = 0  // Control initial setting width

    private val density = context.resources.displayMetrics.density

    init {

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            cvBorderColor = a.getColor(R.styleable.CircleImageView_cv_borderColor, Color.WHITE)
            cvBorderWidth = a.getDimension(R.styleable.CircleImageView_cv_borderWidth, 0f).toInt()
            a.recycle() // защита от неэффективного использования ресурсов
        }

        cvBitmapPaint.isAntiAlias = true // Turn on anti-aliasing
        cvBorderPaint.color = cvBorderColor  // Set the border color
        cvBorderPaint.isAntiAlias = true
    }

    /**
     * Load image
     * load the bitmap
     */
    private fun loadBitmap() {
        if (drawable != null) {
            cvBitmap = (this.drawable as BitmapDrawable).bitmap
        }
    }

    /**
     * Crop picture
     * Get a picture of the middle square of the picture
     * @param bitmap original image
     * @param edgeLength The length of the square to be cropped
     * @return Bitmap Picture of the middle square of the picture
     */
    private fun centerSquareScaleBitmap(bitmap: Bitmap?, edgeLength: Int): Bitmap? {

        if (bitmap == null || edgeLength <= 0) {
            return bitmap
        }
        var result = bitmap
        val widthOrg = bitmap.width // ширина оригинальной картинки
        val heightOrg = bitmap.height // высота оригинальной картинки

        // Make sure that the width of the image is greater than the length of the square to be cropped.
        if (widthOrg >= edgeLength && heightOrg >= edgeLength) {
            // Get the length of the other longer side corresponding to the aspect ratio
            val longerEdge =
                (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg))
            val scaledWidth = if (widthOrg > heightOrg) longerEdge else edgeLength
            val scaledHeight = if (widthOrg > heightOrg) edgeLength else longerEdge

            val scaledBitmap: Bitmap? // Define a new compressed picture bitmap
            try {
                // Compress the image to create a new bitmap in a new size
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true)
            } catch (e: Exception) {
                return bitmap
            }

            // Get the X-axis offset of the cut intermediate position graphic
            val xTopLeft = (scaledWidth - edgeLength) / 2
            // Get the Y-axis offset of the cut intermediate position graphic
            val yTopLeft = (scaledHeight - edgeLength) / 2
            try {
                // Crop a new square bitmap at the specified offset position
                result =
                    Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength)
                // Free up memory, recycle resources
                scaledBitmap.recycle()
            } catch (e: Exception) {
                return bitmap
            }
        }
        return result
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec) // Get width data mode
        widthSpecSize = MeasureSpec.getSize(widthMeasureSpec) // Get width data

        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec) // Get height data mode
        heightSpecSize = MeasureSpec.getSize(heightMeasureSpec) // Get height data

        // Initialize the height of the control to the image diameter + the width of the two borders (left and right borders)
        widthOrHeight = cvBitmapDiameter + (cvBorderWidth * 2)

        // Judging the width and high data format
        if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
            if (widthSpecSize > heightSpecSize) { // If the width and height are given data
                // If the height is small, the image diameter is equal to the height minus the width of the two borders
                cvBitmapDiameter = heightSpecSize - (cvBorderWidth * 2f)
                widthOrHeight = heightSpecSize.toFloat() // control width is height
            } else {
                // If the width is small, the image diameter is equal to the width minus the width of the two borders
                cvBitmapDiameter = widthSpecSize - (cvBorderWidth * 2f)
                widthOrHeight = widthSpecSize.toFloat() // control width is width
            }
        } else if (widthSpecMode == MeasureSpec.EXACTLY) {
            // If only the width is given, the image diameter is equal to the width minus the width of the two borders
            cvBitmapDiameter = widthSpecSize - (cvBorderWidth * 2f)
            widthOrHeight = widthSpecSize.toFloat() // control width is width
        } else if (heightSpecMode == MeasureSpec.EXACTLY) {
            // If only height is given, the image diameter is equal to the height minus the width of the two borders
            cvBitmapDiameter = heightSpecSize - (cvBorderWidth * 2f)
            widthOrHeight = heightSpecSize.toFloat() // control width is height
        }
        // Save the measured width and height, round up and add 2 to ensure that the canvas is enough to draw the border and shadow
        setMeasuredDimension((widthOrHeight + 2).toInt(), (widthOrHeight + 2).toInt())
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        loadBitmap()
        cvBitmap = centerSquareScaleBitmap(
            cvBitmap,
            (cvBitmapDiameter + 1).toInt()
        ) // Crop the picture to get the middle square


        if (cvBitmap != null && canvas != null) {
            cvBitmapShader = BitmapShader(cvBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            cvBitmapPaint.shader = cvBitmapShader

            // отрисовка границ
            canvas.drawCircle(
                widthOrHeight / 2, widthOrHeight / 2,
                cvBitmapDiameter / 2 + cvBorderWidth, cvBorderPaint
            )
            // отрисовка картинки
            canvas.drawCircle(
                widthOrHeight / 2, widthOrHeight / 2,
                cvBitmapDiameter / 2, cvBitmapPaint
            )
        }
    }

    /**
     * Изменение ширины границы
     * dp = (int - 0.5f)/density
     * Resources.getSystem().displayMetrics.density
     * @param borderWidth
     */
    fun setBorderWidth(@Dimension borderWidth: Int) {
        cvBorderWidth = Math.round(borderWidth * density + 0.5f)
        this.invalidate()
    }


    /**
     * Получение значения ширины границы
     */
    @Dimension
    fun getBorderWidth(): Int {
        return Math.round((cvBorderWidth - 0.5f) / density)
    }

    /**
     * Изменение цвета границы
     * @param hex
     */
    fun setBorderColor(hex: String) {
        cvBorderPaint.color = Color.parseColor(hex)
        cvBorderColor = cvBorderPaint.color
        this.invalidate()
    }

    /**
     * Изменение цвета границы
     * @param borderColor
     */
    fun setBorderColor(borderColor: Int) {
        if (isColorResource(borderColor)) {
            cvBorderPaint.color = context.getColor(borderColor)
        } else {
            cvBorderPaint.color = borderColor
        }
        cvBorderColor = cvBorderPaint.color
        this.invalidate()
    }


    /**
     * Получение цвета границы
     */
    fun getBorderColor(): Int = cvBorderColor


    /**
     * Является ли числове значение идентификатором ресурса
     */
    private fun isColorResource(value: Int): Boolean {
        return try {
            ResourcesCompat.getColor(resources, value, null)
            true
        } catch (e: NotFoundException) {
            false
        }
    }
}
