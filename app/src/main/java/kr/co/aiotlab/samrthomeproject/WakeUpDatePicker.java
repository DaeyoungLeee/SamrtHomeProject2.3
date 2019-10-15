package kr.co.aiotlab.samrthomeproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

import static kr.co.aiotlab.samrthomeproject.SetWakeUp.setting_day;
import static kr.co.aiotlab.samrthomeproject.SetWakeUp.setting_month;
import static kr.co.aiotlab.samrthomeproject.SetWakeUp.setting_year;

public class WakeUpDatePicker extends DialogFragment {

    int year_pick, month_pick, day_pick;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();

        // setting된 값이 존재하면 이전에 저장된 값을 화면에 뿌려주자
        if (!(setting_year < 1980)){
            calendar.set(setting_year, setting_month - 1, setting_day);
        }

        // datepicker에서 선택한 날짜를 보내준다.
        year_pick = calendar.get(Calendar.YEAR);
        month_pick = calendar.get(Calendar.MONTH);
        day_pick = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year_pick, month_pick, day_pick);
    }
}
