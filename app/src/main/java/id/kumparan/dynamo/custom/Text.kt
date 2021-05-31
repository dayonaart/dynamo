package id.kumparan.dynamo.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.text.Spannable
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.util.Base64
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

class EditText : androidx.appcompat.widget.AppCompatEditText {
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    )

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?) : super(context!!)

    override fun setText(text: CharSequence, type: BufferType) {
        val spannable = getTextWithImages(
            context, text,
            lineHeight, currentTextColor
        )
        super.setText(spannable, BufferType.SPANNABLE)
    }

    companion object {
        private const val DRAWABLE = "drawable"
        const val PATTERN = "\\Q[img src=\\E([a-zA-Z0-9_]+?)\\Q/]\\E"
        private fun getTextWithImages(
            context: Context,
            text: CharSequence,
            lineHeight: Int,
            colour: Int
        ): Spannable {
            val spannable = Spannable.Factory.getInstance().newSpannable(text)
            addImages(context, spannable, lineHeight, colour)
            return spannable
        }

        private fun addImages(
            context: Context,
            spannable: Spannable,
            lineHeight: Int,
            colour: Int
        ): Boolean {
            val refImg = Pattern.compile(PATTERN)
            var hasChanges = false
            val matcher = refImg.matcher(spannable)
            while (matcher.find()) {
                var set = true
                for (span in spannable.getSpans(
                    matcher.start(), matcher.end(),
                    ImageSpan::class.java
                )) {
                    if (spannable.getSpanStart(span) >= matcher.start()
                        && spannable.getSpanEnd(span) <= matcher.end()
                    ) {
                        spannable.removeSpan(span)
                    } else {
                        set = false
                        break
                    }
                }
                val resName = spannable.subSequence(matcher.start(1), matcher.end(1)).toString()
                    .trim { it <= ' ' }
                val id = context.resources.getIdentifier(resName, DRAWABLE, context.packageName)
                if (set) {
                    hasChanges = true
                    spannable.setSpan(
                        makeImageSpan(context, id, lineHeight, colour),
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            return hasChanges
        }

        private fun makeImageSpan(
            context: Context,
            drawableResId: Int,
            size: Int,
            colour: Int
        ): ImageSpan {
            val drawable = ContextCompat.getDrawable(context, drawableResId)
            val d = BitmapFactory.decodeResource(context.resources, drawableResId)
            val bitmap = BitmapDrawable(context.resources, d)
            val drawable64=encodeImage(bitmap.bitmap)
            println("BIT $drawable64")
            drawable?.mutate()
            drawable?.setBounds(0, 0, 800, 450)
            return ImageSpan(d, ImageSpan.ALIGN_BOTTOM)
        }
        private fun encodeImage(bm: Bitmap) :String{
            val baos = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val b = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        }
    }
}