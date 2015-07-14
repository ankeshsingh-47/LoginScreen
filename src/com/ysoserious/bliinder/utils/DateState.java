package com.ysoserious.bliinder.utils;

import com.ysoserious.bliinder.R;

public enum DateState {
	AWAITING_ANSWER, ITS_A_DATE, NEVER_EVER, NOT_FILLED_PARTNER_FILLED, NOT_FILLED_PARTNER_NOT_FILLED;

	public static int getImageResource(DateState state) {
		switch (state) {
		case NOT_FILLED_PARTNER_FILLED:
			return R.drawable.exclamation_mark;
		case NOT_FILLED_PARTNER_NOT_FILLED:
			return R.drawable.exclamation_mark;
		case AWAITING_ANSWER:
			return R.drawable.question_mark;
		case ITS_A_DATE:
			return R.drawable.rightcheck_2;
		case NEVER_EVER:
			return R.drawable.button_check_2;
		default:
			return -1;
		}
	}

	@Override
	public String toString() {
		switch (this) {
		case NOT_FILLED_PARTNER_FILLED:
		case NOT_FILLED_PARTNER_NOT_FILLED:
			return "You need to fill your preferences..";
		case AWAITING_ANSWER:
			return "Waiting for an answer from your partner";
		case ITS_A_DATE:
			return "It's A Date! :)";
		case NEVER_EVER:
			return "Never Ever :(";
		}
		return "Date State Does Not Exist";
	}
}
