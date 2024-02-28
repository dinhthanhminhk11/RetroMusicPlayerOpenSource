package code.name.monkey.retromusic.views.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import code.name.monkey.retromusic.databinding.LayoutDialogConfirmBinding

class DialogConfirmCustom(
    context: Context,
    private val content: String?,
    private val onAccessClick: () -> Unit,
    private val onCancelClick: () -> Unit

) : Dialog(context) {
    private lateinit var binding: LayoutDialogConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        binding = LayoutDialogConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.content.text = content


        binding.close.setOnClickListener {
            dismiss()
        }

        binding.cancelButton.setOnClickListener {
            onCancelClick.invoke()
            dismiss()
        }

        binding.confirmButton.setOnClickListener {
            onAccessClick.invoke()
            dismiss()
        }

    }

    companion object {
        fun create(
            context: Context,
            content: String?,
            onAccessClick: () -> Unit,
            onCancelClick: () -> Unit,
        ): DialogConfirmCustom {
            return DialogConfirmCustom(context, content, onAccessClick, onCancelClick)
        }
    }
}