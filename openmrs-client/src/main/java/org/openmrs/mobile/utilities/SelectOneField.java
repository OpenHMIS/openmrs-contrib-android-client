package org.openmrs.mobile.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import org.openmrs.mobile.models.Answer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectOneField implements Serializable, Parcelable {
	
	public static final Creator<SelectOneField> CREATOR = new Creator<SelectOneField>() {
		@Override
		public SelectOneField createFromParcel(Parcel source) {
			return new SelectOneField(source);
		}
		
		@Override
		public SelectOneField[] newArray(int size) {
			return new SelectOneField[size];
		}
	};
	private String concept = null;
	private Answer chosenAnswer = null;
	private List<Answer> answerList;
	
	public SelectOneField(List<Answer> answerList, String concept) {
		this.answerList = answerList;
		this.concept = concept;
	}
	
	protected SelectOneField(Parcel in) {
		this.concept = in.readString();
		this.chosenAnswer = (Answer) in.readSerializable();
		this.answerList = new ArrayList<Answer>();
		in.readList(this.answerList, Answer.class.getClassLoader());
	}
	
	public void setAnswer(int answerPosition) {
		if (answerPosition < answerList.size()) {
			chosenAnswer = answerList.get(answerPosition);
		}
		if (answerPosition == -1) {
			chosenAnswer = null;
		}
	}
	
	public Answer getChosenAnswer() {
		return chosenAnswer;
	}
	
	public void setChosenAnswer(Answer chosenAnswer) {
		this.chosenAnswer = chosenAnswer;
	}
	
	public String getConcept() {
		return concept;
	}
	
	public int getChosenAnswerPosition() {
		return answerList.indexOf(chosenAnswer);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.concept);
		dest.writeSerializable(this.chosenAnswer);
		dest.writeList(this.answerList);
	}
}
