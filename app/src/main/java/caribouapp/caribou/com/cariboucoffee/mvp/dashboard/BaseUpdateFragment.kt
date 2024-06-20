package caribouapp.caribou.com.cariboucoffee.mvp.dashboard

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import caribouapp.caribou.com.cariboucoffee.R
import caribouapp.caribou.com.cariboucoffee.SourceApplication
import caribouapp.caribou.com.cariboucoffee.mvp.BaseFragment
import caribouapp.caribou.com.cariboucoffee.util.AppUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

abstract class BaseUpdateFragment<T : ViewDataBinding?> : BaseFragment<T>(), UpdateContract.View {
    lateinit var mUpdatePresenter: UpdateContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        val presenter = BaseUpdatePresenter(this)
        SourceApplication[requireContext()].component.inject(presenter)
        mUpdatePresenter = presenter
        return view
    }

    override fun onResume() {
        super.onResume()
        runUpdates()
    }

    private fun runUpdates() {
        CoroutineScope(IO).launch {
            mUpdatePresenter.runUpdates()
        }
    }

    override fun showRecommendUpdate() {
        val recommendUpdateDialog = AlertDialog.Builder(context)
            .setTitle(R.string.recommend_update_dialog_title)
            .setMessage(getString(R.string.recommend_update_dialog_text))
            .setNegativeButton(R.string.update_later) { _: DialogInterface?, _: Int -> mUpdatePresenter.snoozeUpdate() }
            .setPositiveButton(R.string.update_now) { dialog: DialogInterface, _: Int ->
                AppUtils.goToPlayStore(requireContext())
                dialog.dismiss()
            }
            .create()
        recommendUpdateDialog.show()
    }

    override fun showVersionIncompatible() {
        val versionIncompatibleDialog = AlertDialog.Builder(context)
            .setTitle(R.string.update)
            .setMessage(getString(R.string.version_too_old_message))
            .setNeutralButton(R.string.update_now) { _: DialogInterface?, _: Int ->
                AppUtils.goToPlayStore(
                    context
                )
            }
            .create()
        versionIncompatibleDialog.setOnDismissListener { _: DialogInterface? -> requireActivity().finish() }
        versionIncompatibleDialog.show()
    }
}
