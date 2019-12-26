package com.YuanWai.tools.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/SortFolder")
public class SortFileController {
	private static final Logger log = LoggerFactory.getLogger(SortFileController.class);

	private static final String FOLDER_ADDR = "G:\\我的雲端硬碟\\私藏\\好物\\簡中字幕";

	private static final String REQUEST_INDEX = "";
	private static final String RESPONSE_INDEX = "/success";

	/**
	 * 掃描演員資料夾
	 * 
	 * @param file
	 * @return
	 */
	public static List<String> findFolder(File file) {
		List<String> nameList = new ArrayList<String>();
		try {
			if (file.listFiles() != null) {
				for (final File fileEntry : file.listFiles()) {
					if (fileEntry.exists()) {
						if (fileEntry.isDirectory()) {
							if (fileEntry.getName().length() < 8) {
								nameList.add(fileEntry.getName());
							}
							findFolder(fileEntry);
						} else {
							// log.error("已達資料夾最底層");
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("預載掃描資料夾發生 Exception :{}", e);
		}
		return nameList;
	}

	/**
	 * 掃描所有資料夾
	 * 
	 * @param file
	 * @return
	 */
	public static List<String> findAllFolder(File file) {
		List<String> nameList = new ArrayList<String>();
		try {
			if (file.listFiles() != null) {
				for (final File fileEntry : file.listFiles()) {
					int count = 0;
					if (fileEntry.exists()) {
						if (fileEntry.isDirectory()) {
							count = count + 1;
							if (1 == count) {
								/* 只掃描第一層 */
								nameList.add(fileEntry.getName());
							}
							findAllFolder(fileEntry);
						} else {
							// log.error("已達資料夾最底層");
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("預載掃描資料夾發生 Exception :{}", e);
		}
		return nameList;
	}

	@RequestMapping(value = REQUEST_INDEX, method = { RequestMethod.GET, RequestMethod.POST })
	public String sortFolder(Model model) {
		File file = new File(FOLDER_ADDR);
		List<String> nameList = findFolder(file);
		List<String> allList = findAllFolder(file);
		AtomicInteger count = new AtomicInteger(0);
		;
		try {
			nameList.stream().forEach(i -> {
				allList.stream().forEach(j -> {
					if (j.contains(i) && !j.equals(i)) {
						count.addAndGet(1);
						String folderAddr = FOLDER_ADDR + "/";
						File fromFolder = new File(folderAddr + j);
						File aimsFolder = new File(folderAddr + i);
						File newFolder = new File(folderAddr + i + "/" + j);
						try {
							newFolder.mkdirs();
							Files.move(fromFolder.toPath(), newFolder.toPath(),
									java.nio.file.StandardCopyOption.ATOMIC_MOVE);
							Thread.sleep(2000);
							log.info("formFolder " + fromFolder.toPath());
							log.info("aimsFolder " + aimsFolder.toPath());
						} catch (IOException e) {
							log.error("移動檔案發生錯誤 " + e);
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						log.info("已找到相符檔案 將 「" + fromFolder.getName() + "」 移至 「" + aimsFolder.getName() + " 」資料夾");
					}
				});
			});
			log.info("已全數整理完畢，共整理" + count.get() + "組資料夾");
		} catch (Exception e) {
			log.error("發生例外錯誤 " + e);
			e.printStackTrace();
		}
		return RESPONSE_INDEX;
	}

	public static void main(String[] args) {
//		File file = new File(FOLDER_ADDR);
//		List<String> nameList = findFolder(file);
//		List<String> allList = findAllFolder(file);
//		AtomicInteger count = new AtomicInteger(0);;
//		try {
//			nameList.stream().forEach(i -> {
//				allList.stream().forEach(j -> {
//					if (j.contains(i) && !j.equals(i)) {
//						count.addAndGet(1);
//						String folderAddr = FOLDER_ADDR + "/";
//						File fromFolder = new File(folderAddr + j);
//						File aimsFolder = new File(folderAddr + i);
//						File newFolder = new File(folderAddr + i + "/" + j);
//						try {
//							newFolder.mkdirs();
//							Files.move(fromFolder.toPath(), newFolder.toPath(),
//									java.nio.file.StandardCopyOption.ATOMIC_MOVE);
//							Thread.sleep(2000);
//							log.info("formFolder " + fromFolder.toPath());
//							log.info("aimsFolder " + aimsFolder.toPath());
//						} catch (IOException e) {
//							log.error("移動檔案發生錯誤 " + e);
//							e.printStackTrace();
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						log.info("已找到相符檔案 將 「" + fromFolder.getName() + "」 移至 「" + aimsFolder.getName() + " 」資料夾");
//					}
//				});
//			});
//			log.info("已全數整理完畢，共整理" + count.get() + "組資料夾");
//		} catch (Exception e) {
//			log.error("發生例外錯誤 " + e);
//			e.printStackTrace();
//		}
	}
}
