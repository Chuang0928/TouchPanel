/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.samsung.spensdk.example.tools;

import com.samsung.spensdk.example.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PreferencesOfOtherOption extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	//==============================
	// Preference Key Constants 
	//==============================
	public static final String		PREF_KEY_HOVER_POINTER_STYLE = "hover_pointer_style";
	public static final String		PREF_KEY_HOVER_POINTER_SHOW_OPTION = "hover_pointer_show_option";
	public static final String		PREF_KEY_PEN_SIDE_BUTTON_STYLE = "pen_side_button_style";	
	public static final String		PREF_KEY_PREDICTIVE_TEXT = "predictive_text_mode";
	public static final String		PREF_KEY_SETTINGVIEW_PINUP = "settingview_pinup_mode";
	public static final String		PREF_KEY_PEN_ONLY_MODE = "pen_only_mode";
	public static final String		PREF_KEY_STROKE_LONGCLICK = "pen_only_mode_stroke_longclick";
	public static final String		PREF_KEY_TEXT_LONGCLICK = "pen_only_mode_text_longclick";
	public static final String		PREF_KEY_HOVER_SCROLL = "hover_scroll";
	public static final String		PREF_KEY_MAINTAIN_SCALE_ON_RESIZE = "maintain_scale_on_resize";
	public static final String		PREF_KEY_MAINTAIN_PEN_COLOR = "maintain_pen_color";
	public static final String		PREF_KEY_SUPPORT_BEAUTIFY_STROKE_SETTING = "support_beautify_stroke_setting";
	public static final String		PREF_KEY_BOUNDARY_TOUCH_SCROLL = "boundary_touch_scroll";

	private ListPreference mListPreferenceHoverPointerStyle;
	private ListPreference mListPreferenceHoverPointerShowOption;
	private ListPreference mListPreferencePenSideButtonStyle;	
	private CheckBoxPreference mCheckPreferencePredictiveText;
	private CheckBoxPreference mCheckPreferenceSettingviewPinup;
	private CheckBoxPreference mCheckPreferencePenOnlyMode;
	private CheckBoxPreference mCheckPreferenceStrokeLongclick;
	private CheckBoxPreference mCheckPreferenceTextLongclick;
	private CheckBoxPreference mCheckPreferenceHoverScroll;
	private CheckBoxPreference mCheckPreferenceMaintainScaleOnResize;
	private CheckBoxPreference mCheckPreferenceMaintainPenColor;
	private CheckBoxPreference mCheckPreferenceSupportBeautifyStrokeSetting;
	private CheckBoxPreference mCheckPreferenceBoundaryTouchScroll;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.otheroptionpreferences);

		mListPreferenceHoverPointerStyle = (ListPreference)getPreferenceScreen().findPreference(PREF_KEY_HOVER_POINTER_STYLE);
		mListPreferenceHoverPointerShowOption = (ListPreference)getPreferenceScreen().findPreference(PREF_KEY_HOVER_POINTER_SHOW_OPTION);
		mListPreferencePenSideButtonStyle = (ListPreference)getPreferenceScreen().findPreference(PREF_KEY_PEN_SIDE_BUTTON_STYLE);   	
		mCheckPreferencePredictiveText = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_PREDICTIVE_TEXT);
		mCheckPreferenceSettingviewPinup = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_SETTINGVIEW_PINUP);
		mCheckPreferencePenOnlyMode = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_PEN_ONLY_MODE);
		mCheckPreferenceStrokeLongclick = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_STROKE_LONGCLICK);
		mCheckPreferenceTextLongclick = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_TEXT_LONGCLICK);
		mCheckPreferenceHoverScroll = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_HOVER_SCROLL);
		mCheckPreferenceMaintainScaleOnResize = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_MAINTAIN_SCALE_ON_RESIZE);
		mCheckPreferenceMaintainPenColor = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_MAINTAIN_PEN_COLOR);
		mCheckPreferenceSupportBeautifyStrokeSetting = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_SUPPORT_BEAUTIFY_STROKE_SETTING);
		mCheckPreferenceBoundaryTouchScroll = (CheckBoxPreference)getPreferenceScreen().findPreference(PREF_KEY_BOUNDARY_TOUCH_SCROLL);
		updatePreferences();
	}

	public void updatePreferences()
	{
		updatePreference(PREF_KEY_HOVER_POINTER_STYLE);
		updatePreference(PREF_KEY_HOVER_POINTER_SHOW_OPTION);
		updatePreference(PREF_KEY_PEN_SIDE_BUTTON_STYLE);	
		updatePreference(PREF_KEY_PREDICTIVE_TEXT);	
		updatePreference(PREF_KEY_SETTINGVIEW_PINUP);	
		updatePreference(PREF_KEY_PEN_ONLY_MODE);	
		updatePreference(PREF_KEY_STROKE_LONGCLICK);	
		updatePreference(PREF_KEY_TEXT_LONGCLICK);	
		updatePreference(PREF_KEY_HOVER_SCROLL);
		updatePreference(PREF_KEY_MAINTAIN_SCALE_ON_RESIZE);
		updatePreference(PREF_KEY_MAINTAIN_PEN_COLOR);	
		updatePreference(PREF_KEY_SUPPORT_BEAUTIFY_STROKE_SETTING);
		updatePreference(PREF_KEY_BOUNDARY_TOUCH_SCROLL);	
	}

	public void updatePreference(String key)
	{
		// Preference change
		String str; 
		if (key.equals(PREF_KEY_HOVER_POINTER_STYLE)) {
			str = mListPreferenceHoverPointerStyle.getValue();
			if(str==null)	
			{
				mListPreferenceHoverPointerStyle.setValueIndex(0);
				str = mListPreferenceHoverPointerStyle.getValue();
			}			
			// Show Selected Text 
			int nSelectIndex = Integer.parseInt(str);			
			CharSequence[] strings = getResources().getTextArray(R.array.hover_pointer_style);
			mListPreferenceHoverPointerStyle.setSummary(strings[nSelectIndex]);			
		}
		else if (key.equals(PREF_KEY_HOVER_POINTER_SHOW_OPTION)) {
			str = mListPreferenceHoverPointerShowOption.getValue();
			if(str==null)	
			{
				mListPreferenceHoverPointerShowOption.setValueIndex(0);
				str = mListPreferenceHoverPointerShowOption.getValue();
			}			
			// Show Selected Text 
			int nSelectIndex = Integer.parseInt(str);			
			CharSequence[] strings = getResources().getTextArray(R.array.hover_pointer_show_option);
			mListPreferenceHoverPointerShowOption.setSummary(strings[nSelectIndex]);			
		}
		else if (key.equals(PREF_KEY_PEN_SIDE_BUTTON_STYLE)) {
			str = mListPreferencePenSideButtonStyle.getValue();
			if(str==null)	
			{
				mListPreferencePenSideButtonStyle.setValueIndex(0);
				str = mListPreferencePenSideButtonStyle.getValue();
			}			
			// Show Selected Text 
			int nSelectIndex = Integer.parseInt(str);			
			CharSequence[] strings = getResources().getTextArray(R.array.side_button_style);
			mListPreferencePenSideButtonStyle.setSummary(strings[nSelectIndex]);			
		}	
		else if (key.equals(PREF_KEY_PREDICTIVE_TEXT)) {
			if(mCheckPreferencePredictiveText.isChecked())
				mCheckPreferencePredictiveText.setSummaryOn(getResources().getString(R.string.predictive_text_enable));
			else
				mCheckPreferencePredictiveText.setSummaryOff(getResources().getString(R.string.predictive_text_disable));
		}
		else if (key.equals(PREF_KEY_SETTINGVIEW_PINUP)) {
			if(mCheckPreferenceSettingviewPinup.isChecked())
				mCheckPreferenceSettingviewPinup.setSummaryOn(getResources().getString(R.string.settingview_pinup_enable));
			else
				mCheckPreferenceSettingviewPinup.setSummaryOff(getResources().getString(R.string.settingview_pinup_disable));
		}
		else if (key.equals(PREF_KEY_PEN_ONLY_MODE)) {
			if(mCheckPreferencePenOnlyMode.isChecked())
				mCheckPreferencePenOnlyMode.setSummaryOn(getResources().getString(R.string.pen_only_mode_enable));
			else
				mCheckPreferencePenOnlyMode.setSummaryOff(getResources().getString(R.string.pen_only_mode_disable));
		}else if (key.equals(PREF_KEY_STROKE_LONGCLICK)) {
			if(mCheckPreferenceStrokeLongclick.isChecked())
				mCheckPreferenceStrokeLongclick.setSummaryOn(getResources().getString(R.string.stroke_longclick_enable));
			else
				mCheckPreferenceStrokeLongclick.setSummaryOff(getResources().getString(R.string.stroke_longclick_disable));
		}
		else if (key.equals(PREF_KEY_TEXT_LONGCLICK)) {
			if(mCheckPreferenceTextLongclick.isChecked())
				mCheckPreferenceTextLongclick.setSummaryOn(getResources().getString(R.string.text_longclick_enable));
			else
				mCheckPreferenceTextLongclick.setSummaryOff(getResources().getString(R.string.text_longclick_disable));
		}
		else if (key.equals(PREF_KEY_HOVER_SCROLL)) {
			if(mCheckPreferenceHoverScroll.isChecked())
				mCheckPreferenceHoverScroll.setSummaryOn(getResources().getString(R.string.hover_scroll_enable));
			else
				mCheckPreferenceHoverScroll.setSummaryOff(getResources().getString(R.string.hover_scroll_disable));
		}	
		else if (key.equals(PREF_KEY_MAINTAIN_SCALE_ON_RESIZE)) {
			if(mCheckPreferenceMaintainScaleOnResize.isChecked())
				mCheckPreferenceMaintainScaleOnResize.setSummaryOn(getResources().getString(R.string.maintain_scale_on_resize_enable));
			else
				mCheckPreferenceMaintainScaleOnResize.setSummaryOff(getResources().getString(R.string.maintain_scale_on_resize_disable));
		}
		else if (key.equals(PREF_KEY_MAINTAIN_PEN_COLOR)) {
			if(mCheckPreferenceMaintainPenColor.isChecked())
				mCheckPreferenceMaintainPenColor.setSummaryOn(getResources().getString(R.string.maintain_pen_color_enable));
			else
				mCheckPreferenceMaintainPenColor.setSummaryOff(getResources().getString(R.string.maintain_pen_color_disable));
		}				
		else if (key.equals(PREF_KEY_SUPPORT_BEAUTIFY_STROKE_SETTING)) {
			if(mCheckPreferenceSupportBeautifyStrokeSetting.isChecked())
				mCheckPreferenceSupportBeautifyStrokeSetting.setSummaryOn(getResources().getString(R.string.support_beautify_stroke_enable));
			else
				mCheckPreferenceSupportBeautifyStrokeSetting.setSummaryOff(getResources().getString(R.string.support_beautify_stroke_disable));
		}
		else if (key.equals(PREF_KEY_BOUNDARY_TOUCH_SCROLL)) {
			if(mCheckPreferenceBoundaryTouchScroll.isChecked())
				mCheckPreferenceBoundaryTouchScroll.setSummaryOn(getResources().getString(R.string.boundary_touch_scroll_enable));
			else
				mCheckPreferenceBoundaryTouchScroll.setSummaryOff(getResources().getString(R.string.boundary_touch_scroll_disable));
		}
	}

	// Return Load Canvas Size converting option
	public static final int getPreferenceHoverPointerStyle(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		String strLoadCanvasSizePref = prefs.getString(PREF_KEY_HOVER_POINTER_STYLE, "3");	// default
		return Integer.parseInt(strLoadCanvasSizePref);
	}
	// Return Load Canvas Align converting option
	public static final int getPreferenceHoverPointerShowOption(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		String strLoadCanvasHAlignPref = prefs.getString(PREF_KEY_HOVER_POINTER_SHOW_OPTION, "1");	// default
		return Integer.parseInt(strLoadCanvasHAlignPref);
	}
	// Return Load Canvas Align converting option
	public static final int getPreferencePenSideButtonStyle(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		String strLoadCanvasVAlignPref = prefs.getString(PREF_KEY_PEN_SIDE_BUTTON_STYLE, "0");	// default
		return Integer.parseInt(strLoadCanvasVAlignPref);
	}	

	public static final boolean getPreferencePredictiveText(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_PREDICTIVE_TEXT, false);	// default
	}

	public static final boolean getPreferenceSettingviewPinup(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_SETTINGVIEW_PINUP, false);	// default
	}

	public static final boolean getPreferencePenOnlyMode(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_PEN_ONLY_MODE, true);	// default
	}

	public static final boolean getPreferenceStrokeLongclick(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_STROKE_LONGCLICK, true);	// default
	}

	public static final boolean getPreferenceTextLongclick(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_TEXT_LONGCLICK, true);	// default
	}

	public static final boolean getPreferenceHoverScroll(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_HOVER_SCROLL, true);	// default
	}

	public static final boolean getPreferenceMaintainScaleOnResize(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_MAINTAIN_SCALE_ON_RESIZE, false);	// default
	}

	public static final boolean getPreferenceMaintainPenColor(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_MAINTAIN_PEN_COLOR, false);	// default
	}

	public static final boolean getPreferenceSupportBeautifyStrokeSetting(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_SUPPORT_BEAUTIFY_STROKE_SETTING, true);	// default
	}

	public static final boolean getPreferenceBoundaryTouchScroll(Context context)
	{
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean(PREF_KEY_BOUNDARY_TOUCH_SCROLL, true);	// default
	}

	@Override
	protected void onResume() {
		super.onResume();		
		//  Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);        
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}    

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		updatePreference(key);		
	}

}
