package com.example.yoga_application

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.content.contentValuesOf
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class YogaDatabaseHelper (context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    private val baseURL = "http://10.0.2.2/UniversalYogaSite/"
    companion object{
        //course
        private const val DATABASE_NAME="yoga.db"
        private const val DATABASE_VERSION=1
        private const val TABLE_NAME_COURSE="course"
        private const val COLUMN_ID="id"
        private const val COLUMN_NAME="course"
        private const val COLUMN_DAY="day"
        private const val COLUMN_TIME="time"
        private const val COLUMN_CAPACITY="capacity"
        private const val COLUMN_PRICE="price"
        private const val COLUMN_TYPE="type"
        private const val COLUMN_DESCRIPTION="description"
        //class
        private const val TABLE_NAME_CLASS = "class"
        private const val COLUMN_DATE = "date_of_class"
        private const val COLUMN_TEACHER = "teacher"
        private const val COLUMN_COMMENT = "comment"
        private const val COLUMN_COURSE_ID = "courseId"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery="CREATE TABLE IF NOT EXISTS $TABLE_NAME_COURSE ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT, $COLUMN_DAY TEXT, $COLUMN_TIME TEXT, $COLUMN_CAPACITY INT,$COLUMN_PRICE FLOAT, $COLUMN_TYPE TEXT, $COLUMN_DESCRIPTION TEXT)"

        db?.execSQL(createTableQuery)

        val query2 = "create table if not exists $TABLE_NAME_CLASS" +
                " ($COLUMN_ID integer primary key autoincrement," +
                "$COLUMN_DATE text, $COLUMN_TEACHER text, $COLUMN_COMMENT text, $COLUMN_COURSE_ID integer, " +
                "foreign key($COLUMN_COURSE_ID) references $TABLE_NAME_COURSE($COLUMN_ID)" +
                " on delete cascade)"

        db?.execSQL(query2)
        Log.d("DB","Table created!")
    }

    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
        db?.execSQL("PRAGMA foreign_keys=ON")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery="DROP TABLE IF EXISTS $TABLE_NAME_COURSE"
        db?.execSQL(dropTableQuery)

        val dropTableQuery2="DROP TABLE IF EXISTS $TABLE_NAME_CLASS"
        db?.execSQL(dropTableQuery2)

        Log.d("DB","table upgraded!")
        onCreate(db)
    }
    fun insertCourse(course: Course){
        val db=writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, course.courseName)
            put(COLUMN_DAY, course.day)
            put(COLUMN_TIME, course.time)
            put(COLUMN_CAPACITY, course.capacity)
            put(COLUMN_PRICE, course.price)
            put(COLUMN_TYPE, course.type)
            put(COLUMN_DESCRIPTION, course.description)
        }
        db.insert(TABLE_NAME_COURSE, null, values)
        db.close()
    }
    fun insertClass(classModel: Class){
        val db=writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, classModel.date)
            put(COLUMN_TEACHER, classModel.teacher)
            put(COLUMN_COMMENT, classModel.comment)
            put(COLUMN_COURSE_ID, classModel.courseId)
        }
        db.insert(TABLE_NAME_CLASS, null, values)
        db.close()
    }
    fun getAllCourses():List<Course>{
        val courseList= mutableListOf<Course>()
        val db=readableDatabase
        val query="SELECT * FROM $TABLE_NAME_COURSE"
        val cursor=db.rawQuery(query, null)
        while (cursor.moveToNext()){
            val id=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val courseName=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val day=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY))
            val time=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
            val capacity=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY))
            val price=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE))
            val type=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
            val description=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))

            val course=Course(id, courseName, day, time,
                capacity.toString(), price.toString(), type, description)
            courseList.add(course)
        }
        cursor.close()
        db.close()
        return courseList
    }
    fun getAllClasses():List<Class>{
        val classList= mutableListOf<Class>()
        val db=readableDatabase
        val query="SELECT * FROM $TABLE_NAME_CLASS"
        val cursor=db.rawQuery(query, null)
        while (cursor.moveToNext()){
            val id=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val date=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
            val teacher=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER))
            val comment=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT))
            val courseId=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID))

            val classAdd=Class(id, date, teacher, comment, courseId)
            classList.add(classAdd)
        }
        cursor.close()
        db.close()
        return classList
    }
    fun updateCourse(course: Course){
        val db= writableDatabase
        val values= contentValuesOf().apply {
            put(COLUMN_NAME, course.courseName)
            put(COLUMN_DAY, course.day)
            put(COLUMN_TIME, course.time)
            put(COLUMN_CAPACITY, course.capacity)
            put(COLUMN_PRICE, course.price)
            put(COLUMN_TYPE,course.type)
            put(COLUMN_DESCRIPTION, course.description)
        }
        val whereClause="$COLUMN_ID = ?"
        val whereArgs= arrayOf(course.id.toString())
        db.update(TABLE_NAME_COURSE,values,whereClause,whereArgs)
        db.close()
    }
    fun updateClass(classModel: Class){
        val db= writableDatabase
        val values= contentValuesOf().apply {
            put(COLUMN_DATE, classModel.date)
            put(COLUMN_TEACHER, classModel.teacher)
            put(COLUMN_COMMENT, classModel.comment)
            put(COLUMN_COURSE_ID, classModel.courseId)
        }
        val whereClause="$COLUMN_ID = ?"
        val whereArgs= arrayOf(classModel.id.toString())
        db.update(TABLE_NAME_CLASS,values,whereClause,whereArgs)
        db.close()
    }
    fun getCourseByID(courseId:Int):Course{
        val db=readableDatabase
        val query="SELECT * FROM $TABLE_NAME_COURSE WHERE $COLUMN_ID= $courseId"
        val cursor=db.rawQuery(query,null)
        cursor.moveToFirst()

        val id=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val courseName=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
        val day=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY))
        val time=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
        val capacity=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY))
        val price=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE))
        val type=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
        val description=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))

        cursor.close()
        db.close()
        return Course(id, courseName, day, time, capacity.toString(),
            price.toString(), type, description)
    }
    fun getClassByID(classId:Int): ClassWithCourse? {
        val db=readableDatabase
        val query="SELECT yc.*, ycl.* FROM $TABLE_NAME_CLASS ycl JOIN $TABLE_NAME_COURSE yc ON ycl.$COLUMN_COURSE_ID=yc.$COLUMN_ID WHERE ycl.$COLUMN_ID= $classId"
        val cursor=db.rawQuery(query, null)
        var classData: ClassWithCourse? = null
        if (cursor.moveToFirst()) {
                val id=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val date_of_class=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val teacher=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER))
                val comment=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT))
                val courseId=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID))
                val course_name=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val course_day=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY))
                val course_time=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
                val course_capacity=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY))
                val course_price=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE))
                val course_type=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
                val course_description=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
            // Create the Class object with the retrieved data
            classData = ClassWithCourse(
                classId = id,
                teacher = teacher,
                date = date_of_class,
                comment = comment,
                courseId = courseId,
                courseName = course_name,
                day = course_day,
                time = course_time,
                capacity = course_capacity,
                price = course_price,
                type = course_type,
                description = course_description
            )
        }

        cursor.close()
        db.close()
        return classData
    }
    fun deleteCourse(courseId:Int){
        val db=writableDatabase
        val whereClause="$COLUMN_ID = ?"
        val whereArgs = arrayOf(courseId.toString())

        db.delete(TABLE_NAME_COURSE,whereClause,whereArgs)
        db.close()
    }

    fun deleteClass(classId:Int){
        val db=writableDatabase
        val whereClause="$COLUMN_ID = ?"
        val whereArgs = arrayOf(classId.toString())

        db.delete(TABLE_NAME_CLASS,whereClause,whereArgs)
        db.close()
    }


    fun deleteAll() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME_COURSE")
        db.execSQL("DELETE FROM $TABLE_NAME_CLASS")
        db.close()
    }

    fun getCourseJsonObjectList():MutableList<JSONObject>{

        val db = this.readableDatabase

        val query = "select * from $TABLE_NAME_COURSE"

        var cursor: Cursor? = null

        cursor = db.rawQuery(query,null)

        val courseList = mutableListOf<JSONObject>()

        if(cursor.moveToFirst()){
            do{
                val id=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val courseName=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val day=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY))
                val time=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
                val capacity=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPACITY))
                val price=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRICE))
                val type=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
                val description=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))

                val jsonObj = JSONObject().apply {
                    put("id",id)
                    put("courseName",courseName)
                    put("day", day)
                    put("time", time)
                    put("capacity", capacity)
                    put("price", price)
                    put("type", type)
                    put("description",description)
                }
                courseList.add(jsonObj)

            }while(cursor.moveToNext())
        }
        return courseList
    }//getCourseJsonObjectList

    fun getClassJsonObjectList():MutableList<JSONObject>{

        val db = this.readableDatabase

        val query = "select * from $TABLE_NAME_CLASS"

        var cursor: Cursor? = null

        cursor = db.rawQuery(query,null)

        val classList = mutableListOf<JSONObject>()

        if(cursor.moveToFirst()){
            do{
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val dateClass = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val teacher= cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER))
                val comment= cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMMENT))
                val courseID= cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID))


                val jsonObj = JSONObject().apply {
                    put("id",id)
                    put("date_of_class", dateClass)
                    put("teacher", teacher)
                    put("comment",comment)
                    put("course_id", courseID)
                }
                classList.add(jsonObj)

            }while(cursor.moveToNext())
        }
        return classList
    }//getClassJsonObjectList

    //search start
    fun getFilteredClasses(teacherQuery: String = "", dateQuery: String = "", dayQuery: String = ""): List<Class> {
        val db = this.readableDatabase
        val query = """
        SELECT c.*, d.$COLUMN_DAY FROM $TABLE_NAME_CLASS c 
        JOIN $TABLE_NAME_COURSE d ON c.$COLUMN_COURSE_ID = d.$COLUMN_ID
        WHERE 1=1
        ${if (teacherQuery.isNotEmpty()) "AND c.$COLUMN_TEACHER LIKE ?" else ""}
        ${if (dateQuery.isNotEmpty()) "AND c.$COLUMN_DATE = ?" else ""}
        ${if (dayQuery.isNotEmpty()) "AND d.$COLUMN_DAY = ?" else ""}
    """.trimIndent()

        val selectionArgs = mutableListOf<String>()
        if (teacherQuery.isNotEmpty()) selectionArgs.add("%$teacherQuery%")
        if (dateQuery.isNotEmpty()) selectionArgs.add(dateQuery)
        if (dayQuery.isNotEmpty()) selectionArgs.add(dayQuery)

        val cursor = db.rawQuery(query, selectionArgs.toTypedArray())
        val classes = mutableListOf<Class>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val teacher = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val courseId =
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COURSE_ID)).toString()
                val day =
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY)) // Foreign key day

                classes.add(Class(id, date, teacher, courseId, day))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return classes
    }
    //search end
}