package com.xxx.uhfreader;

import com.android.hdhe.uhf.reader.SerialPort;

public class UhfReaderDevice {

    private static UhfReaderDevice readerDevice;
    private static SerialPort devPower;

    public static UhfReaderDevice getInstance()
    {
        if (devPower == null)
        {
            try
            {
                devPower = new SerialPort();
            }
            catch (Exception e)
            {
                return null;
            }
            devPower.uhfPowerOn();
        }

        if (readerDevice == null) {
            readerDevice = new UhfReaderDevice();
        }

        return readerDevice;
    }

    public void powerOn()
    {
        devPower.uhfPowerOn();
    }

    public void powerOff()
    {
        if (devPower != null) {
            devPower.uhfPowerOff();
            devPower = null;
        }
    }
}

