package tech.appclub.loanmanager.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LoanDAO {

    @Query("SELECT * FROM loan_table WHERE loan_status = 0 ORDER BY loan_payment_date ASC")
    fun getUnpaidLoans(): LiveData<List<Loan>>

    @Query("SELECT * FROM loan_table WHERE loan_status = 1 ORDER BY loan_payment_date ASC")
    fun getPaidLoans(): LiveData<List<Loan>>

    @Query("SELECT * FROM loan_table ORDER BY loan_payment_date ASC")
    fun getAllLoans(): LiveData<List<Loan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(loan: Loan)

    @Query("DELETE FROM loan_table")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteLoan(loan: Loan)

    @Update
    suspend fun updateLoan(loan: Loan)

    @Query("SELECT COUNT(*) FROM loan_table WHERE loan_status = 0")
    fun totalLoanCount(): LiveData<Int>

    @Query("SELECT SUM(loan_amount) FROM loan_table WHERE loan_status = 0")
    fun grandLoanAmount(): LiveData<Double>

}