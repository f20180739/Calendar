package com.example.calendarapp;

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.calendarapp.CalendarAdapter.OnItemListener
import kotlinx.android.synthetic.main.activity_main.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity(), OnItemListener {
    private var monthYearText: TextView? = null
    private var calendarRecyclerView: RecyclerView? = null
    private var selectedDate: LocalDate? = null
    private var selectedDate1: LocalDate? = null
    private var date: Button? = null
    private var retweek: Button? = null
    var cal = Calendar.getInstance()
    var flag:Int=0
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initWidgets()
        selectedDate = LocalDate.now()
        selectedDate1 = LocalDate.now()
        setMonthView()
        date = this.button
        retweek=this.button1

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {

            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                //When Choose Date is clicked
                selectedDate1= LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId()).toLocalDate()
                if(flag==1)
                {val period = Period.between(selectedDate, selectedDate1)
                var mon : Long = period.getMonths().toLong()
                if(mon>0)
                {
                    selectedDate = selectedDate!!.plusMonths(mon)
                }
                else
                {
                    selectedDate = selectedDate!!.minusMonths(mon)
                }

                selectedDate=selectedDate1
                setMonthView()
                onItemClick(1, dayOfMonth.toString())
                }
                //When Retrieve week is clicked
                else if(flag==2)
                {   selectedDate1= LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId()).toLocalDate()
                    selectedDate=selectedDate1
                    onItemClick(1, dayOfMonth.toString())
                    val daysInWeekArray = ArrayList<String>()
                    for(i in 1..7)
                    {cal.set(Calendar.DAY_OF_WEEK,i)
                    selectedDate1= LocalDateTime.ofInstant(cal.toInstant(), cal.getTimeZone().toZoneId()).toLocalDate()
                        //stores days of that week in daysInWeekArray
                    daysInWeekArray.add(cal.get(Calendar.DAY_OF_MONTH).toString()+"\n" +monthYearFromDate(selectedDate1))}
                    setWeekView(daysInWeekArray,cal.get(Calendar.WEEK_OF_YEAR))

                }
        }}

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@MainActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)

                ).show()
                flag=1
            }
        })
        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        retweek!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(
                    this@MainActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
                flag=2
            }
        })
    }

    private fun initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView)
        monthYearText = findViewById(R.id.monthYearTV)
    }
    //Grid view for dates of month
    private fun setMonthView() {
        monthYearText!!.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)
        val calendarAdapter = CalendarAdapter(daysInMonth, this)
        val layoutManager: LayoutManager = GridLayoutManager(applicationContext, 7)
        calendarRecyclerView!!.layoutManager = layoutManager
        calendarRecyclerView!!.adapter = calendarAdapter
    }
    //To fill 6 rows with 7 values each either with "" or with the date
    private fun daysInMonthArray(date: LocalDate?): ArrayList<String> {
        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = selectedDate!!.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value
        for (i in 1..42) {
            if (i <= dayOfWeek && dayOfWeek!=7  || i > daysInMonth + dayOfWeek ) {
                daysInMonthArray.add("")
            } else if(dayOfWeek==7 && i<=daysInMonth)
            {
                daysInMonthArray.add((i).toString())
            }
            else if(dayOfWeek!=7) {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthArray
    }
    //When retrive week is used, displays the week
    private fun setWeekView(daysInWeekArray: ArrayList<String>,weeknumber: Int) {
        monthYearText!!.text = "Week number: $weeknumber"
        val calendarAdapter = CalendarAdapter(daysInWeekArray, this)
        val layoutManager: LayoutManager = GridLayoutManager(applicationContext, 7)
        calendarRecyclerView!!.layoutManager = layoutManager
        calendarRecyclerView!!.adapter = calendarAdapter
    }
    //To get month and Year from date in desired format
    private fun monthYearFromDate(date: LocalDate?): String {
        val formatter = DateTimeFormatter.ofPattern("MMM yyyy")
        return date!!.format(formatter)
    }
    //To go to previous month
    fun previousMonthAction(view: View?) {
        selectedDate = selectedDate!!.minusMonths(1)
        setMonthView()
    }
    //To go to next month
    fun nextMonthAction(view: View?) {
        selectedDate = selectedDate!!.plusMonths(1)
        setMonthView()
    }

    override fun onItemClick(position: Int, dayText: String?) {
        if (dayText != "") {
            val message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate)
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

        }
    }
}


