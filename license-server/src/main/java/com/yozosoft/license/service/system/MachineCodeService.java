package com.yozosoft.license.service.system;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Base64;
import java.util.Enumeration;

public class MachineCodeService {
    private String machineCode;

    public String getFeatureCode() {
        return encryptStr(getMachineCode());
    }

    public String decryptMachineCode(String featureCode) {
        byte[] byteEnCode;
        try {
            byteEnCode = Base64.getDecoder().decode(featureCode);
            return new String(byteEnCode, "UTF-8");
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * k8s环境下getDockerContainerID无法生效
     * 通过此方式获取Docker内的系统UUID
     *
     * @return
     */
    public String getSystemUuid() {
        String result = "";
        Runtime rt = Runtime.getRuntime();
        try {
            Process proc = rt.exec("sudo cat /sys/class/dmi/id/product_uuid");
            InputStreamReader isr = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                result += line;
            }
            isr.close();
            result = result.trim();
            if (StringUtils.isBlank(result)) {
                return null;
            }
            return result;
        } catch (IOException e) {
            return null;
        }
    }

    public String getMachineCode() {
        if (machineCode == null) {
            String machineCode = null;
            String mac = getMac();
            if (mac != null) {
                machineCode = "mac=".concat(mac);
            }
            String ip = getRealIp();
            if (ip != null) {
                ip = "ip=".concat(ip);
                machineCode = machineCode.concat("&").concat(ip);
            }
            String cpu = getCPUSerial();
            if (cpu != null) {
                cpu = "cpuid=".concat(cpu);
                machineCode = machineCode.concat("&").concat(cpu);
            }
            String docUuid = getSystemUuid();
            if (docUuid != null) {
                docUuid = "docUuid=".concat(docUuid);
                machineCode = machineCode.concat("&").concat(docUuid);
            }
            this.machineCode = machineCode;
        }
        return machineCode;
    }

    public String getRealIp() {
        try {
            String localip = null;
            String netip = null;

            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            boolean finded = false;
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1) {
                        netip = ip.getHostAddress();
                        finded = true;
                        break;
                    } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1) {
                        localip = ip.getHostAddress();
                    }
                }
            }
            if (netip != null && !"".equals(netip)) {
                return netip;
            } else {
                return null;
            }
        } catch (SocketException e) {
            return null;
        }
    }

    public String getMacAddress() {
        StringBuffer sb = null;
        try {
            NetworkInterface network = getNetwork();
            if (network == null) {
                return null;
            }
            byte[] mac = network.getHardwareAddress();
            if (mac == null) {
                return null;
            }
            sb = new StringBuffer();
            for (int i = 0; i < mac.length; i++) {
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
        } catch (SocketException e) {
            return null;
        }
        return sb.toString().toUpperCase();
    }

    private NetworkInterface getNetwork() throws SocketException {
        Enumeration<NetworkInterface> networks = NetworkInterface.getNetworkInterfaces();
        if (networks == null) {
            return null;
        }
        NetworkInterface network;
        while (networks.hasMoreElements()) {
            network = networks.nextElement();
            if (network != null && !network.getName().equalsIgnoreCase("lo") && network.getHardwareAddress() != null) {
                return network;
            }
        }
        return null;
    }

    public String getMac() {
        String os = System.getProperty("os.name").toLowerCase();
        String mac1 = getMacAddress();
        String realMac = "";
        if (mac1 != null && mac1.length() > 0) {
            realMac += mac1;
        }
        try {
            String mac2 = null;
            if (os.startsWith("windows")) {
                mac2 = windowsMacAddress();
            } else if (os.startsWith("linux")) {
                mac2 = linuxMacAddress();
            } else if (os.startsWith("unix")) {
                mac2 = unixMacAddress();
            }
            if (mac2 != null && mac2.length() > 0) {
                if (mac1 == null) {
                    realMac += mac2;
                } else if (!mac1.equalsIgnoreCase(mac2)) {
                    realMac += mac2;
                }
            }
        } catch (Exception e) {
            //            e.printStackTrace();
        }
        return realMac;
    }

    private String unixMacAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("ifconfig eth0");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index1 = -1;
            int index2 = -1;
            while ((line = bufferedReader.readLine()) != null) {
                index1 = line.toLowerCase().indexOf("hwaddr");
                //閹垫儳鍩岄敓锟�
                if (index1 > 0) {
                    mac = line.substring(index1 + "hwaddr".length() + 1).trim();
                    break;
                }
                index2 = line.toLowerCase().indexOf("\u786c\u4ef6\u5730\u5740");
                if (index2 > 0) {
                    mac = line.substring(index2 + "\u786c\u4ef6\u5730\u5740".length() + 1).trim();
                    break;
                }
            }
        } catch (IOException e) {
            //            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                //                e.printStackTrace();
            }
            bufferedReader = null;
            process = null;
        }
        if (mac == null) {
            return "";
        }
        return mac.replaceAll("\\W*", "");
    }

    private String linuxMacAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("ifconfig eth0");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index1 = -1;
            int index2 = -1;
            while ((line = bufferedReader.readLine()) != null) {
                index1 = line.toLowerCase().indexOf("hwaddr");
                if (index1 > 0) {
                    mac = line.substring(index1 + "hwaddr".length() + 1).trim();
                    break;
                }
                index2 = line.toLowerCase().indexOf("\u786c\u4ef6\u5730\u5740");
                if (index2 > 0) {
                    mac = line.substring(index2 + "\u786c\u4ef6\u5730\u5740".length() + 1).trim();
                    break;
                }
            }
        } catch (IOException e) {
            //            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                //                e.printStackTrace();
            }
            bufferedReader = null;
            process = null;
        }
        if (mac == null) {
            return "";
        }
        return mac.replaceAll("\\W*", "");

    }

    private String windowsMacAddress() {

        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("ipconfig /all");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index1 = -1;
            int index2 = -1;
            while ((line = bufferedReader.readLine()) != null) {
                index1 = line.toLowerCase().indexOf("physical address");
                if (index1 > 0) {
                    index1 = line.indexOf(":");
                    if (index1 > 0) {
                        mac = line.substring(index1 + 1).trim();
                    }
                    break;
                }
                index2 = line.toLowerCase().indexOf("\u7269\u7406\u5730\u5740");
                if (index2 > 0) {
                    index1 = line.indexOf(":");
                    if (index1 > 0) {
                        mac = line.substring(index1 + 1).trim();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            //            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                //                e.printStackTrace();
            }
            bufferedReader = null;
            process = null;
        }
        if (mac == null) {
            return "";
        }
        return mac.replaceAll("\\W*", "");

    }

    public String encryptStr(String mCode) {
        byte[] byteCode;
        try {
            byteCode = mCode.getBytes("UTF-8");
            String enCode = Base64.getEncoder().encodeToString(byteCode);
            return enCode;
        } catch (UnsupportedEncodingException e) {
            //            e.printStackTrace();
        }
        return null;
    }


    public String getCPUSerial() {
        String result = "";
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            try {
                File file = File.createTempFile("tmp", ".vbs");
                file.deleteOnExit();
                FileWriter fw = new FileWriter(file);

                String vbs = "On Error Resume Next \r\n\r\n" + "strComputer = \".\"  \r\n"
                        + "Set objWMIService = GetObject(\"winmgmts:\" _ \r\n"
                        + "    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n"
                        + "Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n "
                        + "For Each objItem in colItems\r\n " + "    Wscript.Echo objItem.ProcessorId  \r\n "
                        + "    exit for  ' do the first cpu only! \r\n" + "Next                    ";

                fw.write(vbs);
                fw.close();
                Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    result += line;
                }
                input.close();
                file.delete();
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        } else if (os.startsWith("Linux")) {
            String CPU_ID_CMD = "sudo dmidecode -t 4 | grep ID |sort -u |awk -F': ' '{print $2}'";
            Process p;
            try {
                p = Runtime.getRuntime().exec(new String[]{"sh", "-c", CPU_ID_CMD});
                BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    result += line;
                    break;
                }
                br.close();
            } catch (IOException e) {
            }
        }
        if (result.trim().length() < 1 || result == null) {
            result = null;
        }
        if (result != null) {
            return result.trim();
        }
        return result;
    }
}
