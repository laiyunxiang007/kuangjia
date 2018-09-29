package com.example.kuangjia.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * 
 * @Description 用户文件操作
 * 
 */
public class FileUtils {

	private static Log log = LogFactory.getLog(FileUtils.class);

	/**
	 * @Description: 创建目录
	 * @Author Yang Cheng
	 * @Date: Feb 9, 2012 3:20:47 AM
	 * @param path
	 * @return void
	 */
	public static void mkdirs(String path) {
		File uploadFilePath = new File(path);
		// 如果该目录不存在,则创建
		if (!uploadFilePath.exists()) {
			uploadFilePath.mkdirs();
			log.info("目录不存在已创建");
		} else {
			log.info("目录已存在");
		}
	}

	/**
	 * 自定义文件名称
	 * 
	 * @return
	 */
	public static String getFileName() {
		SimpleDateFormat simpledateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Random random = new Random();
		return simpledateFormat.format(new Date()) + random.nextInt(10000);
	}

	/**
	 * 返回指定路径下的所有文件
	 * 
	 * @param path
	 *            路径
	 * @return
	 */
	public static File[] getFiles(String path) {
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		File[] files = folder.listFiles();// 得到当前文件和子文件
		return files;
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 */
	public static void removeFile(File file) {
		if (file.exists()) {// 判断一个文件是否存在
			file.delete();
		}
	}

	public static List<String> readFile(String filePath) throws IOException {
		RandomAccessFile randomFile;
		List<String> readInfoList = new ArrayList<>();
		try {
			// 读取文件
			randomFile = new RandomAccessFile(filePath, "r");
			// 读取一行内容
			String readInfo = randomFile.readLine();
			while (readInfo != null) {
				// 读取进行转码，文件编码原先写入为GBK，但IO流读取默认为ISO-8859-1。先以默认编码读取后在转为GBK不会乱码。
				readInfoList.add(new String(readInfo.getBytes("ISO-8859-1"), "UTF-8"));
				// readInfoList.add(new String(readInfo.getBytes()));
				// 读取一行内容
				readInfo = randomFile.readLine();
			}
			randomFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FileNotFoundException("找不到此路径下的指定文件 filePath:" + filePath);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("程序读取文件出现IO异常");
		} finally {
			// 释放资源
			randomFile = null;
		}
		return readInfoList;
	}

	public static List<String> readFile2(String fileName) {
		List<String> readInfoList = new ArrayList<>();
		try {
			File f = new File(fileName);
			if (f.isFile() && f.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(f), "GBK");
				BufferedReader reader = new BufferedReader(read);
				String line;
				while ((line = reader.readLine()) != null) {
					readInfoList.add(line);
				}
				read.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return readInfoList;
	}

	public static List<String> readFileUtf8(String fileName) {
		List<String> readInfoList = new ArrayList<>();
		System.out.println(fileName);
		try {
			File f = new File(fileName);
			if (f.isFile() && f.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(f), "utf-8");
				BufferedReader reader = new BufferedReader(read);
				String line;
				while ((line = reader.readLine()) != null) {
					readInfoList.add(line);
				}
				read.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return readInfoList;
	}

	public static void writeFile(String filePath, String content, String encoding) throws IOException {
		RandomAccessFile randomFile;
		try {
			randomFile = new RandomAccessFile(filePath, "rw");
			randomFile.seek(randomFile.length());
			randomFile.write((content).getBytes(encoding));
			randomFile.write(("\r\n").getBytes());
			randomFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FileNotFoundException("找不到此路径下的指定文件 filePath:" + filePath);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new UnsupportedEncodingException("不支持的编码类型");
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("向文件写出内容异常");
		} finally {
			randomFile = null;
		}
	}

	public static String transcoding_GBK(String content) {
		if (content == null)
			return null;
		try {
			return new String(content.trim().getBytes("GBK"), "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			log.info("字符串转换编码错误");
		}
		return null;
	}

	public static void downToLocal(String urlPath,String filePath) throws Exception{
		File[] files = getFiles(urlPath);
		int length = files.length;
		URL url = new URL(urlPath);
		for (int i = 0; i < length; i++) {
			File fileItem = files[i];
	        String name = fileItem.getName();
	        String filePaths = filePath+"//"+name;
	        File dirFile = new File(filePath);
	        if (!dirFile.exists()) {
	            dirFile.mkdir();
	        }
	        URLConnection connection = url.openConnection();
	        InputStream in = connection.getInputStream();
	        FileOutputStream os = new FileOutputStream(filePaths);
	        byte[] buffer = new byte[4 * 1024];
	        int read;
	        while ((read = in.read(buffer)) > 0) {
	            os.write(buffer, 0, read);
	        }
	        os.close();
	        in.close();
		}
	}
	
	//删除文件夹
	public static void delFolder(String folderPath) {
	     try {
	        delAllFile(folderPath); //删除完里面所有内容
	        String filePath = folderPath;
	        filePath = filePath.toString();
	        File myFilePath = new File(filePath);
	        myFilePath.delete(); //删除空文件夹
	     } catch (Exception e) {
	       e.printStackTrace();
	     }
	}
	
	//删除指定文件夹下的所有文件
	public static boolean delAllFile(String path) {
	       boolean flag = false;
	       File file = new File(path);
	       if (!file.exists()) {
	         return flag;
	       }
	       if (!file.isDirectory()) { 
	         return flag;
	       }
	       String[] tempList = file.list();
	       File temp = null;
	       for (int i = 0; i < tempList.length; i++) {
	          if (path.endsWith(File.separator)) {
	             temp = new File(path + tempList[i]);
	          } else {
	              temp = new File(path + File.separator + tempList[i]);
	          }
	          if (temp.isFile()) {
	             temp.delete();
	          }
	          if (temp.isDirectory()) {
	             delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
	             //不需要在此调用，直接调用delFolder方法即可完成删除文件和文件夹
	             //delFolder(path + "/" + tempList[i]);
	             flag = true;  
	          }
	       }
	       return flag;
     }

	/**
	 * 判断是否图片类型的文件
	 * @param fileName
	 * @return
	 */
	public static boolean isPicture(String fileName){
		boolean flag = true;
		//获得文件后缀名
		String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();//文件后缀
		//检查是否图片类型的文件
		if(!fileExtension.equals(".gif")&&!fileExtension.equals(".png")&&!fileExtension.equals(".jpg")&&!fileExtension.equals(".jpeg")){
			flag = false;
		}		
		
		return flag;
	}
	
}
