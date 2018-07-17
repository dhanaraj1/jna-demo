package com.ddesk.jna;

import java.awt.Rectangle;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.RECT;

/**
 * static methods to allow Java to call Windows code. user32.dll code is as specified in the JNA interface User32.java
 *
 * @author Pete S
 *
 */
public class JnaUtil {

	private static final User32 user32 = User32.INSTANCE;
	private static Pointer callBackHwnd;

	public static boolean windowExists(final String startOfWindowName) {

		return !user32.EnumWindows((hWnd, userData) -> {
			final byte[] windowText = new byte[512];
			user32.GetWindowTextA(hWnd, windowText, 512);
			final String wText = Native.toString(windowText).trim();

			if (!wText.isEmpty() && wText.startsWith(startOfWindowName)) {
				return false;
			}
			return true;
		}, null);
	}

	public static boolean windowExists(final Pointer hWnd) {

		return user32.IsWindow(hWnd);
	}

	public static Pointer getWinHwnd(final String startOfWindowName) {

		callBackHwnd = null;

		user32.EnumWindows((hWnd, userData) -> {
			final byte[] windowText = new byte[512];
			user32.GetWindowTextA(hWnd, windowText, 512);
			final String wText = Native.toString(windowText).trim();

			if (!wText.isEmpty() && wText.contains(startOfWindowName)) {
				callBackHwnd = hWnd;
				return false;
			}
			return true;
		}, null);
		return callBackHwnd;
	}

	public static boolean setForegroundWindow(final Pointer hWnd) {

		return user32.SetForegroundWindow(hWnd) != 0;
	}

	public static Pointer getForegroundWindow() {

		return user32.GetForegroundWindow();
	}

	public static String getForegroundWindowText() {

		final Pointer hWnd = getForegroundWindow();
		final int nMaxCount = 512;
		final byte[] lpString = new byte[nMaxCount];
		final int getWindowTextResult = user32.GetWindowTextA(hWnd, lpString, nMaxCount);
		if (getWindowTextResult == 0) {
			return "";
		}

		return Native.toString(lpString);
	}

	public static boolean isForegroundWindow(final Pointer hWnd) {

		return user32.GetForegroundWindow().equals(hWnd);
	}

	public static boolean setForegroundWindow(final String startOfWindowName) {

		final Pointer hWnd = getWinHwnd(startOfWindowName);
		return user32.SetForegroundWindow(hWnd) != 0;
	}

	public static Rectangle getWindowRect(final Pointer hWnd) throws JnaUtilException {

		if (hWnd == null) {
			throw new JnaUtilException("Failed to getWindowRect since Pointer hWnd is null");
		}
		Rectangle result = null;
		final RECT rect = new RECT();
		final boolean rectOK = user32.GetWindowRect(hWnd, rect);
		if (rectOK) {
			final int x = rect.left;
			final int y = rect.top;
			final int width = rect.right - rect.left;
			final int height = rect.bottom - rect.top;
			result = new Rectangle(x, y, width, height);
		}

		return result;
	}

	public static Rectangle getWindowRect(final String startOfWindowName) throws JnaUtilException {

		final Pointer hWnd = getWinHwnd(startOfWindowName);
		if (hWnd != null) {
			return getWindowRect(hWnd);
		} else {
			throw new JnaUtilException("Failed to getWindowRect for \"" + startOfWindowName + "\"");
		}
	}

	public static Pointer getWindow(final Pointer hWnd, final int uCmd) {

		return user32.GetWindow(hWnd, uCmd);
	}

	public static String getWindowText(final Pointer hWnd) {

		final int nMaxCount = 512;
		final byte[] lpString = new byte[nMaxCount];
		final int result = user32.GetWindowTextA(hWnd, lpString, nMaxCount);
		if (result == 0) {
			return "";
		}
		return Native.toString(lpString);
	}

	public static Pointer getOwnerWindow(final Pointer hWnd) {

		return user32.GetWindow(hWnd, User32.GW_OWNER);
	}

	public static String getOwnerWindow(final String childTitle) {

		final Pointer hWnd = getWinHwnd(childTitle);
		final Pointer parentHWnd = getOwnerWindow(hWnd);
		if (parentHWnd == null) {
			return "";
		}
		return getWindowText(parentHWnd);

	}

	public static void main(final String[] args) throws InterruptedException {

		final String[] testStrs = { /* "C:\\Users\\dhanraj.khodaliya.TECHFORCE\\Desktop\\new 2.txt - Notepad++", */"DIET" };
		for (final String testStr : testStrs) {
			final Pointer hWnd = getWinHwnd(testStr);
			final boolean isWindow = windowExists(hWnd);
			System.out.printf("%-22s %5b %16s %b%n", testStr, windowExists(testStr), hWnd, isWindow);
		}

		final String ehrProd = "DIET";
		final Pointer hWnd = getWinHwnd(ehrProd);
		System.out.println("is it foreground window? " + isForegroundWindow(hWnd));
		final boolean foo = setForegroundWindow(ehrProd);
		System.out.println("foregroundwindow: " + foo);
		Thread.sleep(400);
		System.out.println("is it foreground window? " + isForegroundWindow(hWnd));
		Thread.sleep(400);
		System.out.println("tesxt " + getWindowText(hWnd));

		/*
		 * try { final Rectangle rect = getWindowRect(ehrProd); final Robot robot = new Robot();
		 *
		 * final BufferedImage img = robot.createScreenCapture(rect); final ImageIcon icon = new ImageIcon(img); final JLabel label = new JLabel(icon);
		 * JOptionPane.showMessageDialog(null, label);
		 *
		 * } catch (final AWTException e) { e.printStackTrace(); } catch (final JnaUtilException e) { e.printStackTrace(); }
		 */
	}

}