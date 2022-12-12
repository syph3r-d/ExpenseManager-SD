package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Database;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class DBTransactionDAO implements TransactionDAO {


    private Context context;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DBTransactionDAO(@Nullable Context context) {
        this.context = context;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase DB = Database.getInstanceDB(context).getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Database.COLUMN_ACC_NO, accountNo);

        contentValues.put(Database.COLUMN_DATE,  dateFormat.format(date));
        contentValues.put(Database.COLUMN_TYPE, String.valueOf(expenseType));
        contentValues.put(Database.COLUMN_AMOUNT, amount);

        DB.insert(Database.TABLE_NAME2 , null , contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        SQLiteDatabase DB = Database.getInstanceDB(context).getWritableDatabase();

        @SuppressLint("Recycle")
        Cursor cursor = DB.rawQuery("SELECT * FROM "+ Database.TABLE_NAME2+";" , null);

        if(cursor.getCount()>0){

            List<Transaction> transactions = new ArrayList<>();

            while (cursor.moveToNext()){
                @SuppressLint("Range") String acc_no = cursor.getString(cursor.getColumnIndex(Database.COLUMN_ACC_NO));
                @SuppressLint("Range") String Strdate = cursor.getString(cursor.getColumnIndex(Database.COLUMN_DATE));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(Database.COLUMN_TYPE));
                @SuppressLint("Range") double amount = cursor.getDouble(cursor.getColumnIndex(Database.COLUMN_AMOUNT));

                ExpenseType expenseType = null;

                if(ExpenseType.EXPENSE.name().equals(type)){
                    expenseType = ExpenseType.EXPENSE;
                }else expenseType = ExpenseType.INCOME;

                try{
                    Date date = dateFormat.parse(Strdate);
                    transactions.add(new Transaction(date , acc_no , expenseType , amount));
                } catch (ParseException e){
                    e.printStackTrace();
                }

            }
            return transactions;

        }else {
            return new ArrayList<Transaction>();
        }
    }

    @SuppressLint("Recycle")
    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {

        SQLiteDatabase DB = Database.getInstanceDB(context).getWritableDatabase();

        List<Transaction> transactions = new ArrayList<>();

        @SuppressLint("Recycle") Cursor cursor = DB.rawQuery("SELECT * FROM "+ Database.TABLE_NAME2+ " LIMIT "+limit+";" , null);

        if(cursor.getCount()<limit){

            while (cursor.moveToNext()){
                @SuppressLint("Range") String acc_no = cursor.getString(cursor.getColumnIndex(Database.COLUMN_ACC_NO));
                @SuppressLint("Range") String Strdate = cursor.getString(cursor.getColumnIndex(Database.COLUMN_DATE));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(Database.COLUMN_TYPE));
                @SuppressLint("Range") double amount = cursor.getDouble(cursor.getColumnIndex(Database.COLUMN_AMOUNT));

                ExpenseType expenseType = ExpenseType.valueOf(type);

                try{
                    Date date = dateFormat.parse(Strdate);
                    transactions.add(new Transaction(date , acc_no , expenseType , amount));
                } catch (ParseException e){
                    e.printStackTrace();
                }

            }
            return transactions;

        }else {
            cursor = DB.rawQuery("SELECT * FROM " + Database.TABLE_NAME2+";" , null);
            for (int i=0;i<limit;i++){
                cursor.moveToNext();
                @SuppressLint("Range") String acc_no = cursor.getString(cursor.getColumnIndex(Database.COLUMN_ACC_NO));
                @SuppressLint("Range") String Strdate = cursor.getString(cursor.getColumnIndex(Database.COLUMN_DATE));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex(Database.COLUMN_TYPE));
                @SuppressLint("Range") double amount = cursor.getDouble(cursor.getColumnIndex(Database.COLUMN_AMOUNT));

                ExpenseType expenseType = null;

                if(ExpenseType.EXPENSE.name().equals(type)){
                    expenseType = ExpenseType.EXPENSE;
                }else expenseType = ExpenseType.INCOME;

                try{
                    Date date = dateFormat.parse(Strdate);
                    transactions.add(new Transaction(date , acc_no , expenseType , amount));
                } catch (ParseException e){
                    e.printStackTrace();
                }
            }

            return transactions;
        }
    }
}