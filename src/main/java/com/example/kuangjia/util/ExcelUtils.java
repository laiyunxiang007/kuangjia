package com.example.kuangjia.util;

import org.apache.commons.lang.time.FastDateFormat;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcelUtils {

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0");// 格式化 number为整

	private static final DecimalFormat DECIMAL_FORMAT_PERCENT = new DecimalFormat("##.00%");//格式化分比格式，后面不足2位的用0补齐

//	private static final DecimalFormat df_per_ = new DecimalFormat("0.00%");//格式化分比格式，后面不足2位的用0补齐,比如0.00,%0.01%

//	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd"); // 格式化日期字符串

	private static final FastDateFormat FAST_DATE_FORMAT = FastDateFormat.getInstance("yyyy/MM/dd");

	private static final DecimalFormat DECIMAL_FORMAT_NUMBER  = new DecimalFormat("0.00E000"); //格式化科学计数器

	private static final Pattern POINTS_PATTERN = Pattern.compile("0.0+_*[^/s]+"); //小数匹配

	/**
	 * 对外提供读取excel 的方法
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static List<List<Object>> readExcel(MultipartFile file) throws IOException {
		String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
		if(Objects.equals("xls", extension) || Objects.equals("xlsx", extension)) {
			return readExcel(file.getInputStream());
		} else {
			throw new IOException("不支持的文件类型");
		}
	}

	/**
	 * 对外提供读取excel 的方法
	 * @param file
	 * @param cls
	 * @return
	 * @throws IOException
	 */
	public static <T> List<T> readExcel(MultipartFile file, Class<T> cls) throws IOException {
		String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
		if(Objects.equals("xls", extension) || Objects.equals("xlsx", extension)) {
			return readExcel(file.getInputStream(), cls);
		} else {
			throw new IOException("不支持的文件类型");
		}
	}

	/**
	 * 读取 office excel
	 *
	 * @param
	 * @return
	 * @throws IOException
	 */
	private static List<List<Object>> readExcel(InputStream inputStream) throws IOException {
		List<List<Object>> list = new LinkedList<>();
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(inputStream);
			int sheetsNumber = workbook.getNumberOfSheets();
			for (int n = 0; n < sheetsNumber; n++) {
				Sheet sheet = workbook.getSheetAt(n);
				Object value = null;
				Row row = null;
				Cell cell = null;
				for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getPhysicalNumberOfRows(); i++) { // 从第二行开始读取
					row = sheet.getRow(i);
					if (StringUtils.isEmpty(row)) {
						continue;
					}
					List<Object> linked = new LinkedList<>();
					for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
						cell = row.getCell(j);
						if (StringUtils.isEmpty(cell)) {
							continue;
						}
						value = getCellValue(cell);
						linked.add(value);
					}
					list.add(linked);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(workbook);
			IOUtils.closeQuietly(inputStream);
		}
		return list;
	}

	/**
	 * 获取excel数据 将之转换成bean
	 *
	 * @param
	 * @param cls
	 * @param <T>
	 * @return
	 */
	private static <T> List<T> readExcel(InputStream inputStream, Class<T> cls) {
		List<T> dataList = new LinkedList<T>();
		Workbook workbook = null;
		try {
			workbook = WorkbookFactory.create(inputStream);
			Map<String, List<Field>> classMap = new HashMap<String, List<Field>>();
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				ExcelColumn annotation = field.getAnnotation(ExcelColumn.class);
				if (annotation != null) {
					String value = annotation.value();
					if (!classMap.containsKey(value)) {
						classMap.put(value, new ArrayList<Field>());
					}
					field.setAccessible(true);
					classMap.get(value).add(field);
				}
			}
			Map<Integer, List<Field>> reflectionMap = new HashMap<Integer, List<Field>>();
			int sheetsNumber = workbook.getNumberOfSheets();
			for (int n = 0; n < sheetsNumber; n++) {
				Sheet sheet = workbook.getSheetAt(n);
				for (int j = sheet.getRow(0).getFirstCellNum(); j < sheet.getRow(0).getLastCellNum(); j++) { //首行提取注解
					Object cellValue = getCellValue(sheet.getRow(0).getCell(j));
					if (classMap.containsKey(cellValue)) {
						reflectionMap.put(j, classMap.get(cellValue));
					}
				}
				Row row = null;
				Cell cell = null;
				for (int i = sheet.getFirstRowNum() + 1; i < sheet.getPhysicalNumberOfRows(); i++) {
					row = sheet.getRow(i);
					T t = cls.newInstance();
					for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
						cell = row.getCell(j);
						if (reflectionMap.containsKey(j)) {
							Object cellValue = getCellValue(cell);
							List<Field> fieldList = reflectionMap.get(j);
							for (Field field : fieldList) {
								try {
									field.set(t, cellValue);
								} catch (Exception e) {
									//logger.error()
								}
							}
						}
					}
					dataList.add(t);
				}
			}
		} catch (Exception e) {
			dataList = null;
		} finally {
			IOUtils.closeQuietly(workbook);
			IOUtils.closeQuietly(inputStream);
		}
		return dataList;
	}

	/**
	 * 获取excel 单元格数据
	 *
	 * @param cell
	 * @return
	 */
	private static Object getCellValue(Cell cell) {
		Object value = null;
		switch (cell.getCellTypeEnum()) {
			case _NONE:
				break;
			case STRING:
				value = cell.getStringCellValue();
				break;
			case NUMERIC:
				if(DateUtil.isCellDateFormatted(cell)){ //日期
					value = FAST_DATE_FORMAT.format(DateUtil.getJavaDate(cell.getNumericCellValue()));//统一转成 yyyy/MM/dd
				} else if("@".equals(cell.getCellStyle().getDataFormatString())
						|| "General".equals(cell.getCellStyle().getDataFormatString())
						|| "0_ ".equals(cell.getCellStyle().getDataFormatString())){
					//文本  or 常规 or 整型数值
					value = DECIMAL_FORMAT.format(cell.getNumericCellValue());
				} else if(POINTS_PATTERN.matcher(cell.getCellStyle().getDataFormatString()).matches()){ //正则匹配小数类型
					value = cell.getNumericCellValue();  //直接显示
				} else if("0.00E+00".equals(cell.getCellStyle().getDataFormatString())){//科学计数
					value = cell.getNumericCellValue();	//待完善
					value = DECIMAL_FORMAT_NUMBER.format(value);
				} else if("0.00%".equals(cell.getCellStyle().getDataFormatString())){//百分比
					value = cell.getNumericCellValue(); //待完善
					value = DECIMAL_FORMAT_PERCENT.format(value);
				} else if("# ?/?".equals(cell.getCellStyle().getDataFormatString())){//分数
					value = cell.getNumericCellValue(); ////待完善
				} else { //货币
					value = cell.getNumericCellValue();
					value = DecimalFormat.getCurrencyInstance().format(value);
				}
				break;
			case BOOLEAN:
				value = cell.getBooleanCellValue();
				break;
			case BLANK:
				//value = ",";
				break;
			default:
				value = cell.toString();
		}
		return value;
	}

	/**
	 * 导出Excel
	 * @param sheetTitle
	 *            sheet名称
	 * @param headers
	 *            列表标题
	 * @param dataset
	 *            内容
	 * @param out
	 */
	// public void exportExcel(String sheetTitle, String[] headers, String[]
	// columns, Collection<T> dataset,
	// OutputStream out, String datePattern) {
	// exportExcelByColumn(sheetTitle, headers, columns, dataset, out, datePattern);
	// }

	/**
	 * 导出 xls格式Excel HSSF
	 * @param title
	 * @param headers
	 * @param columns
	 * @param dataset
	 * @param out
	 * @param pattern
	 */
	public void exportHSExcelByColumn(String title, String[] headers, String[] columns, Collection<T> dataset,
									  OutputStream out, String pattern) {
		Workbook workbook = new SXSSFWorkbook();
		// 生成一个表格
		Sheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为20个字节
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(24);
		// 生成一个 表格标题行样式
		CellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		// 生成一个字体
		Font font = workbook.createFont();
		font.setColor(IndexedColors.WHITE.getIndex());
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		// font.setBoldweight((short)700));
		// 把字体应用到当前的样式
		style.setFont(font);

		// 生成并设置另一个样式 内容的背景
		CellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		// 生成另一个字体
		Font font2 = workbook.createFont();
		font.setBold(true);
		// font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		// 把字体应用到当前的样式
		style2.setFont(font2);

		// 声明一个画图的顶级管理器
		Drawing<?> patriarch = sheet.createDrawingPatriarch();
		// 定义注释的大小和位置
		Comment comment = patriarch.createCellComment(new HSSFClientAnchor(0, 0, 0,
				0, (short)4, 2, (short)6, 5));
		// 设置注释内容
		comment.setString(new HSSFRichTextString("Created By Phil"));
		// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
		comment.setAuthor("phil");

		// 产生表格标题行
		Row row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellStyle(style);
			RichTextString text = new HSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}

		if(StringUtils.isEmpty(pattern)) {
			pattern = "yyyy/MM/dd";
		}
		FastDateFormat instance = FastDateFormat.getInstance(pattern);
		// 遍历集合数据，产生数据行
		Iterator<T> it = dataset.iterator();
		int index = 0;
		int count = 0;
		while (it.hasNext()) {
			index++;
			row = sheet.createRow(index);
			T t = (T) it.next();
			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
			// Field[] fields = t.getClass().getDeclaredFields();
			count = headers.length < columns.length ? headers.length : columns.length;
			for (int i = 0; i < count; i++) {
				Cell cell = row.createCell(i);
				cell.setCellStyle(style2);
				String fieldName = columns[i];
				String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				try {
					Class<? extends Object> tCls = t.getClass();
					Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
					Object value = getMethod.invoke(t, new Object[] {});
					// 判断值的类型后进行强制类型转换
					String textValue = null;
					if (value instanceof Date) {
						Date date = (Date) value;
						textValue = instance.format(date);
					} else if (value instanceof byte[]) {
						// 有图片时，设置行高为60px;
						row.setHeightInPoints(60);
						// 设置图片所在列宽度为80px,注意这里单位的一个换算
						sheet.setColumnWidth(i, (short) (35.7 * 80));
						// sheet.autoSizeColumn(i);
						byte[] bsValue = (byte[]) value;
						ClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 6, index, (short) 6, index);
						anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
						patriarch.createPicture(anchor, workbook.addPicture(bsValue, SXSSFWorkbook.PICTURE_TYPE_JPEG));
					} else {
						// 其它数据类型都当作字符串简单处理
//						if (value != null) {
//							textValue = value.toString();
//							if (textValue.equalsIgnoreCase("VLD")) {
//								textValue = "有效";
//							} else if (textValue.equalsIgnoreCase("IVD")) {
//								textValue = "无效";
//							}
//						} else {
//							textValue = "";
//						}
					}
					// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
					if (textValue != null) {
						Pattern p = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = p.matcher(textValue);
						if (matcher.matches()) {
							// 是数字当作double处理
							cell.setCellValue(Double.parseDouble(textValue));
						} else {
							RichTextString richString = new HSSFRichTextString(textValue);
							Font font3 = workbook.createFont();
							font3.setColor(IndexedColors.BLACK.index); // 内容
							richString.applyFont(font3);
							cell.setCellValue(richString);
						}
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			workbook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(workbook);
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * 导出 xlsx格式Excel XSSF
	 * @param title
	 * @param headers
	 * @param columns
	 * @param dataset
	 * @param out
	 * @param pattern
	 */
	public void exportXSExcelByColumn(String title, String[] headers, String[] columns,
									  Collection<Map<String, Object>> dataset, OutputStream out, String pattern) {
		Workbook workbook = new SXSSFWorkbook();
		// 生成一个表格
		Sheet sheet = workbook.createSheet(title);
		// 设置表格默认列宽度为20个字节
		sheet.setDefaultColumnWidth(20);
		sheet.setDefaultRowHeightInPoints(24);
		// 生成一个 表格标题行样式
		CellStyle style = workbook.createCellStyle();
		// 设置这些样式
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		// 生成一个字体
		Font font = workbook.createFont();
		font.setColor(IndexedColors.WHITE.getIndex());
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		// font.setBoldweight((short)700));
		// 把字体应用到当前的样式
		style.setFont(font);

		// 生成并设置另一个样式 内容的背景
		CellStyle style2 = workbook.createCellStyle();
		style2.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style2.setBorderBottom(BorderStyle.THIN);
		style2.setBorderLeft(BorderStyle.THIN);
		style2.setBorderRight(BorderStyle.THIN);
		style2.setBorderTop(BorderStyle.THIN);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		// 生成另一个字体
		Font font2 = workbook.createFont();
		font.setBold(true);
		// font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		// 把字体应用到当前的样式
		style2.setFont(font2);

		// 声明一个画图的顶级管理器
		Drawing<?> patriarch = sheet.createDrawingPatriarch();
		// 定义注释的大小和位置
		Comment comment = patriarch.createCellComment(new XSSFClientAnchor(0, 0, 0,
				0, (short)4, 2, (short)6, 5));
		//设置注释内容
		comment.setString(new XSSFRichTextString("Created By Phil"));
		// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
		comment.setAuthor("phil");

		// 产生表格标题行
		Row row = sheet.createRow(0);
		for (int i = 0; i < headers.length; i++) {
			Cell cell = row.createCell(i);
			cell.setCellStyle(style);
			RichTextString text = new XSSFRichTextString(headers[i]);
			cell.setCellValue(text);
		}
		if(StringUtils.isEmpty(pattern)) {
			pattern = "yyyy/MM/dd";
		}
		FastDateFormat instance = FastDateFormat.getInstance(pattern);
		// 遍历集合数据，产生数据行
		Iterator<Map<String, Object>> it = dataset.iterator(); // 多个Map集合
		int index = 0;
		int count = 0;
		while (it.hasNext()) {
			index++;
			row = sheet.createRow(index);
			Map<String, Object> map = it.next();
			count = headers.length < columns.length ? headers.length : columns.length;
			for (int i = 0; i < count; i++) {
				Cell cell = row.createCell(i);
				cell.setCellStyle(style2);
				try {
					Object value = map.get(columns[i]);
					// 判断值的类型后进行强制类型转换
					String textValue = null;
					if (value instanceof Date) {
						Date date = (Date) value;
						textValue = instance.format(date);
					} else if (value instanceof byte[]) {
						// 有图片时，设置行高为60px;
						row.setHeightInPoints(60);
						// 设置图片所在列宽度为80px,注意这里单位的一个换算
						sheet.setColumnWidth(i, (short) (35.7 * 80));
						// sheet.autoSizeColumn(i);
						byte[] bsValue = (byte[]) value;
						ClientAnchor anchor = new XSSFClientAnchor(0, 0, 1023, 255, (short) 6, index, (short) 6, index);
						anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
						patriarch.createPicture(anchor, workbook.addPicture(bsValue, Workbook.PICTURE_TYPE_JPEG));
					} else {
						// 其它数据类型都当作字符串简单处理
						if (value != null) {
							textValue = value.toString();
							// if (textValue.equalsIgnoreCase("VLD")) {
							// textValue = "有效";
							// } else if (textValue.equalsIgnoreCase("IVD")) {
							// textValue = "无效";
							// }
						} else {
							textValue = "";
						}
					}
					// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
					if (textValue != null) {
						Pattern p = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = p.matcher(textValue);
						if (matcher.matches()) {
							// 是数字当作double处理
							cell.setCellValue(Double.parseDouble(textValue));
						} else {
							RichTextString richString = new XSSFRichTextString(textValue);
							Font font3 = workbook.createFont();
							font3.setColor(IndexedColors.BLACK.index); // 内容
							richString.applyFont(font3);
							cell.setCellValue(richString);
						}
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			workbook.write(out);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(workbook);
			IOUtils.closeQuietly(out);
		}
	}

	public static void main(String[] args) throws Exception {

	}
}
