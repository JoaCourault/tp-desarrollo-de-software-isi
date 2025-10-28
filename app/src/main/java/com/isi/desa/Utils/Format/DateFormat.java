package com.isi.desa.Utils.Format;

public class DateFormat {
    public static boolean validateDateFormat(String date) {
        // Formato esperado: yyyy-mm-dd
        String regex = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$";
        return date.matches(regex);
    }
}
