package com.chuckerteam.chucker.api.internal.ui.transaction

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.chuckerteam.chucker.R
import com.chuckerteam.chucker.api.internal.data.entity.HttpTransaction
import com.chuckerteam.chucker.api.internal.support.highlight
import java.lang.ref.WeakReference

private const val ARG_TYPE = "type"

internal class TransactionPayloadFragment : Fragment(), TransactionFragment, SearchView.OnQueryTextListener {

    internal lateinit var headers: TextView
    internal lateinit var body: TextView
    internal lateinit var progressBody: ProgressBar

    private var type: Int = 0
    private var transaction: HttpTransaction? = null
    private var originalBody: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments!!.getInt(ARG_TYPE)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chucker_fragment_transaction_payload, container, false)
        headers = view.findViewById(R.id.headers)
        body = view.findViewById(R.id.body)
        progressBody = view.findViewById(R.id.progress_body)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (type == TYPE_RESPONSE) {
            val searchMenuItem = menu!!.findItem(R.id.search)
            searchMenuItem.isVisible = true
            val searchView = searchMenuItem.actionView as SearchView
            searchView.setOnQueryTextListener(this)
            searchView.setIconifiedByDefault(true)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun transactionUpdated(transaction: HttpTransaction) {
        this.transaction = transaction
        populateUI()
    }

    private fun populateUI() {
        transaction?.let {
            if (isAdded) {
                setBodyText(type, it)
                when (type) {
                    TYPE_REQUEST -> setHeaderText(it.getRequestHeadersString(true))
                    TYPE_RESPONSE -> setHeaderText(it.getResponseHeadersString(true))
                }
            }
        }
    }

    private fun setBodyText(type: Int, transaction: HttpTransaction) {
        if (type == TYPE_REQUEST && !transaction.isRequestBodyPlainText ||
                type == TYPE_RESPONSE && !transaction.isResponseBodyPlainText) {
            body.text = getString(R.string.chucker_body_omitted)
            body.visibility = View.VISIBLE
        } else {
            Log.e("NCO", "startingTask")
            FormattedBodyTask(this).execute(transaction)
        }
    }

    private fun setHeaderText(headersString: String) {
        headers.visibility = if (TextUtils.isEmpty(headersString)) View.GONE else View.VISIBLE
        headers.text = Html.fromHtml(headersString)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        if (newText.isNotBlank())
            body.text = originalBody?.highlight(newText)
        else
            body.text = originalBody
        return true
    }

    companion object {

        const val TYPE_REQUEST = 0
        const val TYPE_RESPONSE = 1

        @JvmStatic
        fun newInstance(type: Int): TransactionPayloadFragment {
            return TransactionPayloadFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TYPE, type)
                }
            }
        }

        private class FormattedBodyTask(
                fragment: TransactionPayloadFragment
        ) : AsyncTask<HttpTransaction, Unit, String>() {

            private val fragment: WeakReference<TransactionPayloadFragment> = WeakReference(fragment)
            private val type: Int = fragment.arguments?.getInt(ARG_TYPE) ?: 0

            override fun onPreExecute() {
                fragment.get()?.let {
                    it.progressBody.visibility = View.VISIBLE
                    it.body.visibility = View.GONE
                }
            }

            override fun doInBackground(vararg transactions: HttpTransaction): String {
                if (transactions.size == 1) {
                    return when (type) {
                        TYPE_REQUEST -> transactions[0].getFormattedRequestBody()
                        else -> transactions[0].getFormattedResponseBody()
                    }
                }
                return ""
            }

            override fun onPostExecute(result: String) {
                fragment.get()?.let {
                    it.progressBody.visibility = View.GONE
                    it.body.visibility = View.VISIBLE
                    it.body.text = result
                }
            }
        }
    }
}
