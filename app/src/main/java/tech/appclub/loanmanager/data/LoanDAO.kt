package tech.appclub.loanmanager.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LoanDAO {

    // Get all unpaid loans
    @Query("SELECT * FROM loan_table WHERE loan_status = 0 ORDER BY loan_payment_date ASC")
    fun getUnpaidLoans(): LiveData<List<Loan>>

    // Get all active loans
    @Query("SELECT * FROM loan_table WHERE loan_status = 2 ORDER BY loan_payment_date ASC")
    fun getActiveLoans(): LiveData<List<Loan>>

    // Get all paid and unpaid loans
    @Query("SELECT * FROM loan_table WHERE loan_status <= 1  ORDER BY loan_payment_date ASC")
    fun getHistoryLoans(): LiveData<List<Loan>>

    // Get all unpaid given loans
    @Query("SELECT * FROM loan_table WHERE loan_status = 0 AND loan_situation = 0 ORDER BY loan_payment_date ASC")
    fun getUnpaidGivenLoans(): LiveData<List<Loan>>

    // Get all unpaid taken loans
    @Query("SELECT * FROM loan_table WHERE loan_status = 0 AND loan_situation = 1 ORDER BY loan_payment_date ASC")
    fun getUnpaidTakenLoans(): LiveData<List<Loan>>

    // Get unpaid loan
    @Query("SELECT * FROM loan_table WHERE id = :loanId LIMIT 1")
    suspend fun getLoan(loanId: Int): Loan

    // Get all paid loans
    @Query("SELECT * FROM loan_table WHERE loan_status = 1 ORDER BY loan_payment_date ASC")
    fun getPaidLoans(): LiveData<List<Loan>>

    // Insert a loan
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(loan: Loan)

    // Delete all loans
    @Query("DELETE FROM loan_table")
    suspend fun deleteAll()

    // Delete all paid loans
    @Query("DELETE FROM loan_table WHERE loan_status = 1")
    suspend fun deleteAllPaidLoans()

    // Delete all unpaid loans
    @Query("DELETE FROM loan_table WHERE loan_status = 0")
    suspend fun deleteAllUnPaidLoans()

    // Delete all paid and unpaid loans
    @Query("DELETE FROM loan_table WHERE loan_status <= 1")
    suspend fun deleteHistory()

    // Delete a loan
    @Delete
    suspend fun deleteLoan(loan: Loan)

    // Update a loan
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateLoan(loan: Loan)

    // Update loan currency code
    @Query("UPDATE loan_table SET loan_currency_code = :code, loan_currency_label = :currency, " +
            "loan_currency_country = :country, country_position = :position")
    suspend fun updateCurrency(code: String, currency: String, country: String, position: Int)

    // Get count of all unpaid loans
    @Query("SELECT COUNT(*) FROM loan_table WHERE loan_status = 0")
    fun getUnpaidLoanCount(): LiveData<Int>

    // Get sum of all unpaid loans
    @Query("SELECT SUM(loan_amount) FROM loan_table WHERE loan_status = 0")
    fun getUnpaidLoanGrandTotal(): LiveData<Double>

    // Get count of all paid loans
    @Query("SELECT COUNT(*) FROM loan_table WHERE loan_status = 1")
    fun getPaidLoanCount(): LiveData<Int>

    // Get sum of all paid loans
    @Query("SELECT SUM(loan_amount) FROM loan_table WHERE loan_status = 1")
    fun getPaidLoanGrandTotal(): LiveData<Double>

    // Get count of all loans
    @Query("SELECT COUNT(*) FROM loan_table")
    fun getAllLoanCount(): LiveData<Int>

}