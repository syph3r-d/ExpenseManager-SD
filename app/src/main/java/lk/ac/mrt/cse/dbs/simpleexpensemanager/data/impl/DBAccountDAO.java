package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Database;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class DBAccountDAO implements AccountDAO {

    private Context context;

    public DBAccountDAO(@Nullable Context context) {
        this.context = context;
    }

    @Override
    public List<String> getAccountNumbersList() {

        SQLiteDatabase DB = Database.getInstanceDB(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor cursor = DB.rawQuery("SELECT * FROM "+ Database.TABLE_NAME1+";" , null);
        if(cursor.getCount()>0){
            List<String> accountsNumberList = new ArrayList<>();
            while (cursor.moveToNext()){
                @SuppressLint("Range") String acc_no = cursor.getString(cursor.getColumnIndex(Database.COLUMN_ACC_NO));
                accountsNumberList.add(acc_no);
            }
            return accountsNumberList;
        }else {
            return new ArrayList<String>();
        }
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase DB = Database.getInstanceDB(context).getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM "+ Database.TABLE_NAME1+";" , null);
        if(cursor.getCount()>0){
            List<Account> accounts = new ArrayList<>();
            while (cursor.moveToNext()){
                @SuppressLint("Range") String acc_no = cursor.getString(cursor.getColumnIndex(Database.COLUMN_ACC_NO));
                @SuppressLint("Range") String bank_name = cursor.getString(cursor.getColumnIndex(Database.COLUMN_BANK_NAME));
                @SuppressLint("Range") String holder = cursor.getString(cursor.getColumnIndex(Database.COLUMN_ACC_HOLDER));
                @SuppressLint("Range") double balance = cursor.getDouble(cursor.getColumnIndex(Database.COLUMN_ACC_BALANCE));
                accounts.add(new Account(acc_no , bank_name , holder , balance));
            }
            return accounts;
        }else {
            return new ArrayList<Account>();
        }
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase DB = Database.getInstanceDB(context).getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM "+ Database.TABLE_NAME1+" WHERE "+ Database.COLUMN_ACC_NO+" = ?"+";" , new String[] {accountNo});
        if(cursor.getCount()>0){
            Account account = null;
            while (cursor.moveToNext()){
                @SuppressLint("Range") String acc_no = cursor.getString(cursor.getColumnIndex(Database.COLUMN_ACC_NO));
                @SuppressLint("Range") String bank_name = cursor.getString(cursor.getColumnIndex(Database.COLUMN_BANK_NAME));
                @SuppressLint("Range") String holder = cursor.getString(cursor.getColumnIndex(Database.COLUMN_ACC_HOLDER));
                @SuppressLint("Range") double balance = cursor.getDouble(cursor.getColumnIndex(Database.COLUMN_ACC_BALANCE));
                account = new Account(acc_no , bank_name , holder ,balance);
                break;
            }
            return account;
        }else {
            throw new InvalidAccountException("Account Number "+accountNo+" not found!");
        }
    }

    @Override
    public void addAccount(Account account) {

        try{
            getAccount(account.getAccountNo());
        } catch(InvalidAccountException e){
            SQLiteDatabase DB = Database.getInstanceDB(context).getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Database.COLUMN_ACC_NO, account.getAccountNo());
            contentValues.put(Database.COLUMN_BANK_NAME, account.getBankName());
            contentValues.put(Database.COLUMN_ACC_HOLDER, account.getAccountHolderName());
            contentValues.put(Database.COLUMN_ACC_BALANCE, account.getBalance());
//            long result = DB.insert(DBEditor.TABLE_NAME1 , null , contentValues);
            DB.insert(Database.TABLE_NAME1 , null , contentValues);
        }
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase DB = Database.getInstanceDB(context).getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM "+ Database.TABLE_NAME1+" WHERE "+ Database.COLUMN_ACC_NO+" = ?"+";" , new String[] {accountNo});
        if(cursor.getCount()>0){
            DB.delete(Database.TABLE_NAME1 , "acc_no=?" , new String[]{accountNo});
        }else {
            throw new InvalidAccountException("Account Number "+accountNo+" not found!");
        }
    }

    @SuppressLint("Range")
    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        SQLiteDatabase DB = Database.getInstanceDB(context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor cursor = DB.rawQuery("SELECT * FROM "+ Database.TABLE_NAME1+" WHERE "+ Database.COLUMN_ACC_NO+" = ?"+";" , new String[] {accountNo});

        if(cursor.getCount()>0){
            double pre_balance = 0;
            while (cursor.moveToNext()){
                pre_balance = cursor.getDouble(cursor.getColumnIndex(Database.COLUMN_ACC_BALANCE));
                break;
            }
            double new_balance = -1;
            switch (expenseType) {
                case EXPENSE:
                    new_balance = pre_balance - amount;
                    break;
                case INCOME:
                    new_balance = pre_balance + amount;
                    break;
            }
            contentValues.put("balance" , new_balance);
            DB.update(Database.TABLE_NAME1 , contentValues , "acc_no=?" , new String[]{accountNo});
        }else {
            throw new InvalidAccountException("Account Number "+accountNo+" not found!");
        }
    }
}