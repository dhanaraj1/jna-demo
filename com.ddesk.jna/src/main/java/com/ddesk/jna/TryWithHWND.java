package com.ddesk.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.StdCallLibrary;

public class TryWithHWND {

	public interface User32 extends StdCallLibrary {

		User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

		boolean EnumWindows(WinUser.WNDENUMPROC lpEnumFunc, Pointer arg);

		int GetWindowTextA(HWND hWnd, byte[] lpString, int nMaxCount);
	}

	public static void main(final String[] args) {

		final User32 user32 = User32.INSTANCE;
		user32.EnumWindows((hWnd, arg1) -> {

			final byte[] windowText = new byte[512];
			user32.GetWindowTextA(hWnd, windowText, 512);
			final String wText = Native.toString(windowText);

			// get rid of this if block if you want all windows regardless of whether
			// or not they have text
			if (wText.isEmpty()) {
				return true;
			}

			System.out.println(wText);
			return true;
		}, null);
	}
}
