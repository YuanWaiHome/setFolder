package com.YuanWai.tools.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/SortFolder")
public class SortFileController {
	private static final Logger log = LoggerFactory.getLogger(SortFileController.class);

	private static final String FOLDER_ADDR = "G:\\我的雲端硬碟\\私藏\\好物\\簡中字幕";

	private static final String REQUEST_INDEX = "";
	private static final String REQUEST_SEARCH_FOLDER = "/searchFolder";
	private static final String RESPONSE_INDEX = "/success";
	private static final String RESPONSE_SEARCH_FOLDER = "/searchFolder";
	
	private static final List<String> allList = new ArrayList<String>();

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
							allList.add(fileEntry.getPath());
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

	/**
	 * 整理資料夾
	 * @param model
	 * @return
	 */
	@RequestMapping(value = REQUEST_INDEX, method = { RequestMethod.GET, RequestMethod.POST })
	public String sortFolder(Model model) {
		File file = new File(FOLDER_ADDR);
		List<String> nameList = findFolder(file);
		List<String> allList = findAllFolder(file);
		AtomicInteger count = new AtomicInteger(0);
		AtomicInteger errorFile = new AtomicInteger(0);
		;
		try {
			nameList.stream().forEach(i -> {
				allList.stream().forEach(j -> {
					if (j.contains(i) && !j.equals(i)) {
						String folderAddr = FOLDER_ADDR + "/";
						File fromFolder = new File(folderAddr + j);
						File aimsFolder = new File(folderAddr + i);
						File newFolder = new File(folderAddr + i + "/" + j);
						try {
							newFolder.mkdirs();
							if(fromFolder.exists() && newFolder.exists()) { /* 避免已經移動過 */
								Files.move(fromFolder.toPath(), newFolder.toPath(),
										java.nio.file.StandardCopyOption.ATOMIC_MOVE);
								Thread.sleep(2000);
							}
							count.addAndGet(1);
						} catch (IOException e) {
							log.error("移動檔案發生IOException錯誤 " + e);
							errorFile.addAndGet(1);
							e.printStackTrace();
						} catch (InterruptedException e) {
							log.error("移動檔案發生InterruptedException錯誤 " + e);
							errorFile.addAndGet(1);
							e.printStackTrace();
						}
						log.info("已找到相符檔案 將 「" + fromFolder.getName() + "」 移至 「" + aimsFolder.getName() + " 」資料夾");
					}
				});
			});
		} catch (Exception e) {
			log.error("發生例外錯誤 " + e);
			errorFile.addAndGet(1);
			e.printStackTrace();
		}
		log.info("已全數整理完畢，共成功整理" + count.get() + "組資料夾" + " 共失敗"+errorFile.get()+"筆資料夾");
		
		return RESPONSE_INDEX;
	}

	/**
	 * 搜尋資料夾
	 * @return
	 */
	@RequestMapping(value = REQUEST_SEARCH_FOLDER, method = { RequestMethod.GET, RequestMethod.POST })
	public String searchFolder(Model model,HttpServletRequest request) {
		String searchParam = (String)request.getParameter("searchParam");
		if(allList.size() == 0) {
			log.warn("☆資料夾統整中☆");
			findAllFolder(new File(FOLDER_ADDR));
			log.warn("★資料夾已統整完畢 - 共有{}筆資料夾★",allList.size());
		} else {
			log.warn("★資料夾已統整完畢，可直接開始進行搜尋★");
		}
		List<String> conformPath = new ArrayList<String>();
		if(!StringUtils.isEmpty(searchParam)) {
			allList.stream().forEach(i -> {
				if(i.contains(searchParam.toUpperCase()) || i.contains(searchParam.toLowerCase())) {conformPath.add(i);}
			});
		}
		/* 關鍵字 */
		model.addAttribute("searchParam",searchParam);
		/* 搜尋結果清單 */
		model.addAttribute("conformPath",conformPath);
		return RESPONSE_SEARCH_FOLDER;
	}
	
	
	public static void main(String[] args) {
		
	}
}
