package com.example.lab17_async_tasks;

public interface SClientListener {
	public void setSourceCodeText(String text);
	public void clearSourceCodeText();
	public void showProgressDialog(boolean show);
}
