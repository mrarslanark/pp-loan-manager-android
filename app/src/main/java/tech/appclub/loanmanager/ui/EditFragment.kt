package tech.appclub.loanmanager.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import tech.appclub.loanmanager.MainActivity
import tech.appclub.loanmanager.R
import tech.appclub.loanmanager.adapters.CurrencySpinnerAdapter
import tech.appclub.loanmanager.data.Country
import tech.appclub.loanmanager.data.Loan
import tech.appclub.loanmanager.databinding.FragmentEditBinding
import tech.appclub.loanmanager.utils.DateTimeUtils
import tech.appclub.loanmanager.utils.NumUtils.Companion.trimCommaOfString
import tech.appclub.loanmanager.utils.NumberTextWatcherForThousand
import tech.appclub.loanmanager.utils.ValidationUtils
import tech.appclub.loanmanager.viewmodel.LoanViewModel
import java.util.*

class EditFragment : Fragment() {

    private lateinit var loanViewModel: LoanViewModel
    private val args: EditFragmentArgs by navArgs()

    private lateinit var binding: FragmentEditBinding

    private var receiveDate: Date? = null
    private var paymentDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_nav_back)
        }
        this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit, container, false)
        this.binding.editLoan = this
        loanViewModel = ViewModelProvider(requireActivity()).get(LoanViewModel::class.java)
        this.binding.loan = args.loan
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CurrencySpinnerAdapter(
            requireContext(),
            (requireActivity() as MainActivity).readCountriesData()
        )
        this.binding.countries.adapter = adapter
        this.binding.countries.setSelection(args.loan.position!!)

        receiveDate = args.loan.receivedOn
        paymentDate = args.loan.paymentOn

        this.binding.holderAmountValue.addTextChangedListener(
            NumberTextWatcherForThousand(this.binding.holderAmountValue)
        )

        setSituationValue(args.loan.situation)

    }

    private fun setSituationValue(situation: Int?) {
        when (situation) {
            0 -> binding.givenRadioButton.isChecked = true
            1 -> binding.borrowedRadioButton.isChecked = true
        }
    }

    fun selectReceivedDate() {

        val calendar = Calendar.getInstance()
        calendar.time = args.loan.receivedOn!!

        DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.receivedDate.text = DateTimeUtils.formatDate(calendar.time)
                receiveDate = calendar.time
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun selectPaymentDate() {

        val calendar = Calendar.getInstance()
        calendar.time = args.loan.paymentOn!!

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.paymentDate.text = DateTimeUtils.formatDate(calendar.time)
                paymentDate = calendar.time
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = receiveDate?.time!! - 1000
        datePickerDialog.show()
    }

    fun cancel() {
        findNavController().navigateUp()
    }

    fun editLoanAction() {

        val loan = Loan()
        loan.id = args.loan.id

        val name = this.binding.holderNameValue.text.toString()
        if (ValidationUtils.isEmpty(name, this.binding.holderNameField, requireContext())) return
        if (name != args.loan.holder!!) {
            loan.holder = name
        } else {
            loan.holder = args.loan.holder
        }

        val amount = trimCommaOfString(this.binding.holderAmountValue.text.toString())
        if (ValidationUtils.isEmpty(
                amount, this.binding.holderAmountField, requireContext()
            )
        ) return
        if (amount.toDouble() != args.loan.amount!!) {
            loan.amount = amount.toDouble()
        } else {
            loan.amount = args.loan.amount
        }


        if (this.binding.countries.selectedItemPosition != args.loan.position!!) {
            val countryModel = this.binding.countries.selectedItem as Country
            val code = countryModel.code
            val country = countryModel.country
            val currency = countryModel.currency
            loan.code = code
            loan.country = country
            loan.currency = currency
            loan.position = this.binding.countries.selectedItemPosition
        } else {
            loan.code = args.loan.code
            loan.country = args.loan.country
            loan.currency = args.loan.currency
            loan.position = args.loan.position
        }

        loan.receivedOn = receiveDate
        loan.paymentOn = paymentDate

        loanViewModel.updateLoan(loan)
        findNavController().navigateUp()

    }

}
