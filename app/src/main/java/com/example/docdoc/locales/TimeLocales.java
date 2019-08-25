package com.example.docdoc.locales;

import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.TimeZone;

public class TimeLocales {

   public String timeformated;

    TimeLocales() {

    }

    public TimeLocales(String currentlocale, Long dateData) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateData);

//        TimeZone tz = calendar.getTimeZone();

        int year = calendar.get(Calendar.YEAR);
        String sYear = year+"";
        sYear=sYear.substring(2,4);// 2019 -> 19
        int month = calendar.get(Calendar.MONTH)+1;
        String sMonth = (month<10)?"0"+month:month+"";
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String sDay = (day<10)?"0"+day:day+"";
        int min = calendar.get(Calendar.MINUTE);
        String sMin = (min<10)?"0"+min:min+"";
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String sHour = (hour<10)?"0"+hour:hour+"";

        switch (currentlocale) {
            case "pl_PL":
                timeformated = sYear+"-"+sMonth+"-"+sDay+"\n"+sHour+":"+sMin;
                break;
            case "en_CA":
                timeformated = sYear+"-"+sMonth+"-"+sDay+"\n"+sHour+":"+sMin;
                break;
            case "ru_RU":
                timeformated = sDay+"."+sMonth+"."+sYear+"\n"+sHour+":"+sMin;
                break;
            case "uk_UK":
                timeformated = sDay+"."+sMonth+"."+sYear+"\n"+sHour+":"+sMin;
                break;
            case "de_DE":
                timeformated = sDay+"."+sMonth+"."+sYear+"\n"+sHour+":"+sMin;
                break;
            case "ro_RO":
                timeformated = sDay+"."+sMonth+"."+sYear+"\n"+sHour+":"+sMin;
                break;
            case "en_US":
                timeformated = sMonth+"/"+sDay+"/"+sYear+"\n"+sHour+":"+sMin;
                break;
            case "es_ES":
                timeformated = sDay+"/"+sMonth+"/"+sYear+"\n"+sHour+":"+sMin;
                break;
            case "it_IT":
                timeformated = sDay+"/"+sMonth+"/"+sYear+"\n"+sHour+":"+sMin;
                break;
            case "fr_FR":
                timeformated = sDay+"/"+sMonth+"/"+sYear+"\n"+sHour+":"+sMin;
                break;
            case "en_UK":
                timeformated = sDay+"/"+sMonth+"/"+sYear+"\n"+sHour+":"+sMin;
                break;
            default:
                timeformated = sDay+"."+sMonth+"."+sYear+"\n"+sHour+":"+sMin;
                break;

        }

    }


}


