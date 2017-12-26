package com.example.peppelt.provacar1;

import java.util.Calendar;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		final Calendar c = Calendar.getInstance();
		int ore = c.get(Calendar.HOUR_OF_DAY);
		int minuti = c.get(Calendar.MINUTE);
		return new TimePickerDialog(getActivity(), this,ore, minuti, false);
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub
		((OnOkPressedDialogPicker)(TimePickerFragment.this.getActivity())).onDialogTimeOkPressed(hourOfDay, minute);
		//act.onDialogTimeOkPressed(hourOfDay, minute);
	}

	

}
