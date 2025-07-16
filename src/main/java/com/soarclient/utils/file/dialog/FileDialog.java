package com.soarclient.utils.file.dialog;

import java.io.File;
import java.nio.ByteBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NFDFilterItem;
import org.lwjgl.util.nfd.NativeFileDialog;

public class FileDialog {

	private DialogStatus status;
	private File output;

	public FileDialog() {
		status = DialogStatus.ERROR;
	}

	public void openFile(String name, String... extensions) {

		try (MemoryStack stack = MemoryStack.stackPush()) {

			NFDFilterItem.Buffer filters = NFDFilterItem.malloc(1);

			filters.get(0).name(stack.UTF8(name)).spec(stack.UTF8(String.join(",", extensions)));

			PointerBuffer pp = stack.mallocPointer(1);

			parseResult(NativeFileDialog.NFD_OpenDialog(pp, filters, (ByteBuffer) null), pp);
		}
	}

	public void openFolder() {

		PointerBuffer path = MemoryUtil.memAllocPointer(1);

		try {
			parseResult(NativeFileDialog.NFD_PickFolder(path, (ByteBuffer) null), path);
		} finally {
			MemoryUtil.memFree(path);
		}
	}

	private void parseResult(int result, PointerBuffer path) {

		switch (result) {
		case NativeFileDialog.NFD_OKAY:

			File file = new File(path.getStringUTF8(0));

			if (file != null && file.exists()) {
				status = DialogStatus.SUCCESS;
				output = file;
			}
			break;
		case NativeFileDialog.NFD_CANCEL:
			status = DialogStatus.CANCEL;
			break;
		case NativeFileDialog.NFD_ERROR:
			status = DialogStatus.ERROR;
			break;
		}
	}

	public boolean isSuccess() {
		return status.equals(DialogStatus.SUCCESS);
	}

	public boolean isCancel() {
		return status.equals(DialogStatus.CANCEL);
	}

	public boolean isError() {
		return status.equals(DialogStatus.ERROR);
	}

	public File getOutput() {
		return output;
	}
}