package ru.minyurovevgeniy.squads;

public class Event
{
    String date;
    String weekday;
    String startTime;
    String endTime;
    String notes;
    String isCancelled;

    public Event(String _date,String _weekday, String _startTime, String _endTime, String _notes, String _isCancelled)
    {
        date=_date;
        weekday=_weekday;
        startTime=_startTime;
        endTime=_endTime;
        notes=_notes;
        isCancelled=_isCancelled;
    }
}
