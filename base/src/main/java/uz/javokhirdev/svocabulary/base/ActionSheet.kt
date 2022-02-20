package uz.javokhirdev.svocabulary.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.javokhirdev.extensions.beVisibleIf
import uz.javokhirdev.extensions.onClick
import uz.javokhirdev.svocabulary.presentation.components.databinding.LayoutActionSheetBinding

const val IS_LISTEN_ACTION_VISIBLE = "IS_LISTEN_ACTION_VISIBLE"

class ActionSheet(private val listener: ActionSheetListener) :
    BaseSheetFragment<LayoutActionSheetBinding>() {

    override fun viewBindingInflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutActionSheetBinding = LayoutActionSheetBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isListenActionVisible = arguments?.getBoolean(IS_LISTEN_ACTION_VISIBLE, false) ?: false

        with(binding) {
            textListen.beVisibleIf(isListenActionVisible)

            textCopy.onClick {
                listener.onSetSheetAction(SheetState.Copy)
                dismissAllowingStateLoss()
            }
            textListen.onClick {
                listener.onSetSheetAction(SheetState.Listen)
                dismissAllowingStateLoss()
            }
            textEdit.onClick {
                listener.onSetSheetAction(SheetState.Edit)
                dismissAllowingStateLoss()
            }
            textDelete.onClick {
                listener.onSetSheetAction(SheetState.Delete)
                dismissAllowingStateLoss()
            }
        }
    }

    interface ActionSheetListener {
        fun onSetSheetAction(sheetState: SheetState)
    }

    companion object {

        @JvmStatic
        fun newInstance(
            listener: ActionSheetListener,
            isListenActionVisible: Boolean = false
        ) = ActionSheet(listener).apply {
            arguments = Bundle().apply {
                putBoolean(IS_LISTEN_ACTION_VISIBLE, isListenActionVisible)
            }
        }
    }
}

sealed class SheetState {
    object Copy : SheetState()
    object Listen : SheetState()
    object Edit : SheetState()
    object Delete : SheetState()
}

infix fun SheetState.onCopy(onCopy: SheetState.Copy.() -> Unit): SheetState {
    return when (this) {
        is SheetState.Copy -> {
            onCopy(this)
            this
        }
        else -> this
    }
}

infix fun SheetState.onListen(onListen: SheetState.Listen.() -> Unit): SheetState {
    return when (this) {
        is SheetState.Listen -> {
            onListen(this)
            this
        }
        else -> this
    }
}

infix fun SheetState.onEdit(onEdit: SheetState.Edit.() -> Unit): SheetState {
    return when (this) {
        is SheetState.Edit -> {
            onEdit(this)
            this
        }
        else -> this
    }
}

infix fun SheetState.onDelete(onDelete: SheetState.Delete.() -> Unit): SheetState {
    return when (this) {
        is SheetState.Delete -> {
            onDelete(this)
            this
        }
        else -> this
    }
}