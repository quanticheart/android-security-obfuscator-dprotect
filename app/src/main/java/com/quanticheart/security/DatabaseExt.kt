@file:Suppress("unused")

package com.quanticheart.security

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

val TABLE_LOGIN = "table_login"
val _ID = "_id"
val LOG = "log"
val ID_USER = "id_user"

val Context.database: ProjectDatabase
    get() = ProjectDatabase(this)

fun ProjectDatabase.executeInsert(tableName: String, callback: () -> HashMap<String, String>) {
    execute {
        insert(tableName, callback())
    }
}

fun ProjectDatabase.executeInsert(tableName: String, vararg data: Pair<String, String>) {
    execute {
        val h = hashMapOf<String, String>()
        data.forEach {
            h[it.first] = it.second
        }
        insert(tableName, h)
    }
}

/**
 * DatabaseUtil class makes All Database Related Operation i.e.
 */
class ProjectDatabase(
    private val mContext: Context,
    private val name: String = "${mContext.packageName}.database"
) : SQLiteOpenHelper(mContext, name, null, 1) {
    private var databaseConn: SQLiteDatabase? = null

    private var dataBasePath = ""

    init {
        dataBasePath = mContext.filesDir.path + "/" + name
    }

    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     */
    @Throws(IOException::class)
    fun createDataBase() {
        val dbExist = checkDataBase()
        if (dbExist) {
            // do nothing - database already exist
        } else {
            // By calling this method and empty database will be created into
            // the default system path
            // of your application so we are gonna be able to overwrite that
            // database with our database.
            this.readableDatabase
            try {
                copyDataBase()
            } catch (e: IOException) {
                throw Error("Error copying database")
            }
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private fun checkDataBase(): Boolean {
        val dbFile = File(dataBasePath + name)
        return dbFile.exists()
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transferring bytestream.
     */
    @Throws(IOException::class)
    private fun copyDataBase() {

        // Open your local db as the input stream
        val inputStream = mContext.assets.open("$name.db")

        // Path to the just created empty db
        val outFileName: String = dataBasePath + name

        // Open the empty db as the output stream
        val outputStream: OutputStream = FileOutputStream(outFileName)

        // transfer bytes from the inputfile to the outputfile
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }

        // Close the streams
        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }

    /**
     * Insert MapData into the table
     *
     * @param tableName
     * @param mapData
     * @return insert rows count
     */
    fun insert(tableName: String, mapData: HashMap<String, String>): Int {
        databaseConn?.insertOrThrow(
            tableName,
            null,
            createContentValues(mapData)
        )
        return 1
    }

    /**
     * Delete Data From Table According to WhereCondition
     *
     * @param tableName
     * @param whereConditionString
     * @param whereArgs
     * @return deleted rows count
     */
    fun delete(
        tableName: String,
        whereConditionString: String?,
        whereArgs: Array<String?>?
    ): Int {
        return databaseConn?.delete(tableName, whereConditionString, whereArgs) ?: -1
    }

    /**
     * Get Data From from Database in List of HashMap according to condition
     *
     * @param isDistinct
     * @param tableName
     * @param fields
     * @param whereConditionPart
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return List of HashMap Containing Data
     */
    fun select(
        tableName: String?,
        fields: Array<String>,
        isDistinct: Boolean = false,
        whereConditionPart: String? = null,
        selectionArgs: Array<String?>? = null,
        groupBy: String? = null,
        having: String? = null,
        orderBy: String? = null,
        limit: String? = null,
    ): ArrayList<HashMap<String, String>> {
        var cursor: Cursor? = null
        val mapList = ArrayList<HashMap<String, String>>()
        try {
            cursor = databaseConn!!.query(
                isDistinct,
                tableName,
                fields,
                whereConditionPart,
                selectionArgs,
                groupBy,
                having,
                orderBy,
                limit
            )
            cursor.moveToFirst()
            for (i in 0 until cursor.count) {
                cursor.moveToPosition(i)
                val map = HashMap<String, String>()
                for (j in fields.indices) {
                    map[fields[j]] = cursor.getString(j)
                }
                mapList.add(map)
            }
        } catch (_: Exception) {
            cursor?.close()
        } finally {
            cursor?.close()
        }
        return mapList
    }

    /**
     * Used to get the number of records inside any table.Mainly the query makes
     * it easy to get the values.
     *
     * @param selectionArgs
     * @param query
     * @return [Integer]
     */
    fun rowCount(selectionArgs: Array<String?>?, query: String?): Int {
        return DatabaseUtils.longForQuery(
            databaseConn, query,
            selectionArgs
        ).toInt()
    }

    /**
     * Make Connection with Database
     *
     * @throws IOException
     */
    @Throws(SQLiteException::class, IOException::class)
    private fun connect() {
        databaseConn = this.writableDatabase
    }

    /**
     * DisConnect the Connection With Database
     *
     * @return status
     */
    @Throws(SQLException::class)
    private fun disConnect(): Boolean {
        if (databaseConn != null) {
            databaseConn?.close()
            databaseConn = null
        }
        return true
    }

    /**
     * It Begins the New Transaction
     */
    private fun beginTransaction() {
        databaseConn?.beginTransaction()
    }

    /**
     * It Commits All Database Related Changes
     *
     * @return status
     */
    @Throws(IllegalStateException::class)
    private fun commit(): Boolean {
        databaseConn?.setTransactionSuccessful()
        return true
    }

    /**
     * Ends The Transaction
     *
     * @return status
     */
    private fun endTransaction(): Boolean {
        databaseConn?.endTransaction()
        return true
    }

    /**
     * To Update the Data in Table From Where Condition
     *
     * @param table
     * @param mapData
     * @param whereConditionPart
     * @param whereArgs
     * @return count of updated rows
     */
    fun update(
        table: String?,
        mapData: HashMap<String, String>,
        whereConditionPart: String?,
        whereArgs: Array<String?>?
    ): Int {
        return databaseConn?.update(
            table,
            createContentValues(mapData),
            whereConditionPart,
            whereArgs
        ) ?: -1
    }

    /**
     * Execute any Raw Query and return Result in Cursor
     *
     * @param query
     * @param selectionArgs
     * @return Cursor of respective data
     */
    fun executeRawQuery(query: String?, selectionArgs: Array<String?>?): Cursor? {
        return databaseConn?.rawQuery(
            query,
            selectionArgs
        )
    }

    /**
     * Convert HashMap to ContentValues
     *
     * @param columns
     * @return ContentValues
     */
    private fun createContentValues(columns: HashMap<String, String>): ContentValues {
        val values = ContentValues()
        val it: Iterator<Map.Entry<String, String>> = columns.entries.iterator()
        while (it.hasNext()) {
            val (key, value) = it
                .next()
            values.put(key, value)
        }
        return values
    }

    fun execute(callback: SQLiteDatabase.() -> Unit) {
        connect()
        databaseConn?.callback()
        disConnect()
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            val sb = StringBuilder()
            sb.append(
                "CREATE TABLE IF NOT EXISTS [" + TABLE_LOGIN + "] (" +
                        "  [" + _ID + "] integer primary key autoincrement," +
                        "  [" + LOG + "] TEXT," +
                        "  [" + ID_USER + "] TEXT" +
                        "  );"
            )

            val commands = sb.split(";").toTypedArray()
            for (i in commands.indices) {
                db.execSQL(commands[i].lowercase(Locale.getDefault()))
            }
        } catch (e: Exception) {
            Log.e("ERROR", e.message ?: "")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            val sb = StringBuilder()
            //
//            sb.append("DROP TABLE IF EXISTS  [contatos];");

//            sb.append("ALTER TABLE ["+TABLE_USER+"] ADD COLUMN ["+ACTIVATED+"] TEXT");

//            sb.append("CREATE TABLE IF NOT EXISTS ["+TABLE_USER+"] (\n" +
//                    "  ["+_ID+"] INTEGER, \n" +
//                    "  ["+LOGIN+"] TEXT, \n" +
//                    "  ["+DATE+"] TEXT, \n" +
//                    "  ["+ACTIVATED+"] TEXT, \n" +
//                    "  RAINT [] PRIMARY KEY (["+_ID+"]));");

            val commands = sb.split(";").toTypedArray()
            for (i in commands.indices) {
                db.execSQL(commands[i].lowercase(Locale.getDefault()))
            }
        } catch (_: Exception) {
        }
        onCreate(db)
    }
}