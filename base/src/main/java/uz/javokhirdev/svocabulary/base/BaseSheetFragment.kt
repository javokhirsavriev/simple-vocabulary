package uz.javokhirdev.svocabulary.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import uz.javokhirdev.svocabulary.presentation.components.R

abstract class BaseSheetFragment<VB : ViewBinding> : BottomSheetDialogFragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    protected abstract fun viewBindingInflate(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = viewBindingInflate(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.behavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    val newMaterialShapeDrawable = createMaterialShapeDrawable(bottomSheet)
                    ViewCompat.setBackground(bottomSheet, newMaterialShapeDrawable)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        return dialog
    }

    private fun createMaterialShapeDrawable(bottomSheet: View): MaterialShapeDrawable {
        val shapeAppearanceModel =
            ShapeAppearanceModel.builder(
                requireContext(),
                0,
                R.style.ShapeTopRounded
            ).build()

        val currentMaterialShapeDrawable = bottomSheet.background as MaterialShapeDrawable
        val newMaterialShapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)

        newMaterialShapeDrawable.initializeElevationOverlay(context)
        newMaterialShapeDrawable.fillColor = currentMaterialShapeDrawable.fillColor
        newMaterialShapeDrawable.tintList = currentMaterialShapeDrawable.tintList
        newMaterialShapeDrawable.elevation = currentMaterialShapeDrawable.elevation
        newMaterialShapeDrawable.strokeWidth = currentMaterialShapeDrawable.strokeWidth
        newMaterialShapeDrawable.strokeColor = currentMaterialShapeDrawable.strokeColor
        return newMaterialShapeDrawable
    }
}