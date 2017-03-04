package com.xfwang.bookreading.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.xfwang.bookreading.bean.BookBrief;
import com.xfwang.bookreading.bean.BookEntity;
import com.xfwang.bookreading.bean.BookInfo;
import com.xfwang.bookreading.bean.ChapterText;
import com.xfwang.bookreading.bean.SortGoodItem;
import com.xfwang.bookreading.bean.SortItem;
import com.xfwang.bookreading.bean.SortUpdateItem;
import com.xfwang.bookreading.utils.LogUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.R.attr.mode;

/**
 * Created by xiaofeng on 2017/1/21.
 * api工具类
 */

public class ApiHelper {
    public static final String BIQUGE_URL = "http://www.biquge.com.tw/";    //网站首页，base url
    public static final String XUANHUAN = "xuanhuan/";      //玄幻
    public static final String XIUZHEN = "xiuzhen/";        //修真
    public static final String DUSHI = "dushi/";            //都市
    public static final String LISHI = "lishi/";            //历史
    public static final String WANGYOU = "wangyou/";        //网游
    public static final String KEHUAN = "kehuan/";          //科幻
    public static final String KONGBU = "kongbu/";          //恐怖
    public static final String QUANBEN = "quanben/";        //全本

    public static final String PAIHANGBANG = "paihangbang/";        //排行榜

    private static ApiHelper mApiHelper;
    private ApiHelper() {
    }

    public static ApiHelper getInstance(){
        if (mApiHelper == null){
            synchronized (ApiHelper.class){
                if (mApiHelper == null){
                    mApiHelper = new ApiHelper();
                }
            }
        }
        return mApiHelper;
    }

    private static final ExecutorService mThreadPool = Executors.newCachedThreadPool();
    private CallBack mCallBack;
    public interface CallBack{
        void onSuccess(Object obj);
        void onFailure();
    }
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                Object obj = msg.obj;
                if(obj == null){
                    mCallBack.onFailure();
                }else {
                    mCallBack.onSuccess(obj);
                }
        }
    };

    public void updateBookShelfData(final String[] urls, CallBack callBack){
        mCallBack = callBack;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<BookEntity> bookEntityList = getBookEntityList(urls);

                Message msg = new Message();
                msg.obj = bookEntityList;
                mHandler.sendMessage(msg);
            }
        };

        mThreadPool.execute(runnable);
    }

    public void updateSortData(final String url, CallBack callBack){
        mCallBack = callBack;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<SortItem> list = getSortData(url);

                Message msg = new Message();
                msg.obj = list;
                mHandler.sendMessage(msg);
            }
        };

        mThreadPool.execute(runnable);
    }

    /**
     * @param bookBriefList
     * @param bookInfosList
     * 获取发现页面数据
     */
    public static void getFindPagerData(List<BookBrief> bookBriefList, List<List<BookInfo>> bookInfosList){
        try {
            Element element = Jsoup.connect(BIQUGE_URL).get().body();
            //获取首页hotcontent
            Elements elements = element.getElementsByClass("item");
            for (Element e : elements) {
                Element imageE = e.getElementsByClass("image").get(0);

                LogUtils.i("url:" + imageE.html());

                if (bookBriefList != null){
                    BookBrief bean = new BookBrief();
                    bean.setBookUrl(imageE.getElementsByTag("a").get(0).attr("href"));

                    String iconUrl1 = imageE.getElementsByTag("a").get(0).getElementsByTag("img").get(0).attr("data-cfsrc");
                    String iconUrl2 = imageE.getElementsByTag("a").get(0).getElementsByTag("img").get(0).attr("src");
                    if (TextUtils.isEmpty(iconUrl1)){
                        bean.setIconUrl(iconUrl2);
                    }{
                        bean.setIconUrl(iconUrl1);
                    }

                    bean.setBookAuthor(e.getElementsByTag("span").get(0).text());
                    bean.setBookName(e.getElementsByTag("dt").get(0).getElementsByTag("a").get(0).text());
                    bean.setBookBrief(e.getElementsByTag("dd").get(0).text());
                    bookBriefList.add(bean);
                }
            }

            //获取首页novelslist
            Elements elements_rec = element.getElementsByClass("content");
            for (int i = 0; i<elements_rec.size(); i++){
                Element e = elements_rec.get(i);
                if (bookBriefList != null){
                    BookBrief bean = new BookBrief();
                    bean.setBookUrl(e.getElementsByTag("dt").get(0).getElementsByTag("a").get(0).attr("href"));

                    String iconUrl1 = e.getElementsByTag("img").get(0).attr("data-cfsrc");
                    String iconUrl2 = e.getElementsByTag("img").get(0).attr("src");
                    if (TextUtils.isEmpty(iconUrl1)){
                        bean.setIconUrl(iconUrl2);
                    }{
                        bean.setIconUrl(iconUrl1);
                    }

//                    bean.setIconUrl(e.getElementsByTag("img").get(0).attr("data-cfsrc"));
//                    bean.setIconUrl(e.getElementsByTag("img").get(0).attr("src"));
                    bean.setBookName(e.getElementsByTag("dt").get(0).getElementsByTag("a").get(0).text());
                    bean.setBookBrief(e.getElementsByTag("dd").get(0).text());
                    bookBriefList.add(bean);
                }

                    Elements elements_li = e.getElementsByTag("li");

                    List<BookInfo> bookInfos = new ArrayList<>();
                    if (bookInfosList != null){
                        for (Element e_li : elements_li) {
                            BookInfo bookInfo = new BookInfo();
                            bookInfo.setBookUrl(e_li.getElementsByTag("a").get(0).attr("href"));
                            bookInfo.setBookName(e_li.text());
                            bookInfos.add(bookInfo);
                        }
                    }

                bookInfosList.add(bookInfos);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * @param url   书籍详情及目录页的url
     * @return
     * 获取书籍详情页的数据
     */
    public static List<BookBrief> getBookDetailData(String url){
        List<BookBrief> bookBriefList = new ArrayList<>();
        try {
            Elements elements = Jsoup.connect(url).get().getElementsByClass("box_con");
            Element element1 = elements.get(0);

            BookBrief bookBrief = new BookBrief();
            bookBrief.setBookBrief(element1.getElementById("intro").text());
            bookBrief.setBookType(element1.getElementsByClass("con_top").get(0).text().split(">")[1].trim());
            bookBrief.setBookAuthor(element1.getElementById("info").getElementsByTag("p").get(0).text().substring(7));
            bookBrief.setBookName(element1.getElementById("info").getElementsByTag("h1").text());
            bookBrief.setLastTime(element1.getElementById("info").getElementsByTag("p").get(2).text());
            bookBrief.setLastChapter(element1.getElementById("info").getElementsByTag("p").get(3).text());
            bookBrief.setLastChapterUrl(url + element1.getElementById("info").getElementsByTag("p").get(3).getElementsByTag("a").attr("href"));
            bookBrief.setIconUrl(BIQUGE_URL + element1.getElementById("fmimg").getElementsByTag("img").attr("src"));
            bookBrief.setBookId(url.replace("//","/").split("/")[2]);
            bookBriefList.add(bookBrief);

            Elements es = elements.get(1).getElementById("list").getElementsByTag("dd");
            for (Element e : es) {
                BookBrief bean = new BookBrief();
                bean.setBookId(url.replace("//","/").split("/")[2]);
                bean.setChapterName(e.text());
                bean.setChapterUrl(BIQUGE_URL + e.getElementsByTag("a").attr("href"));
                bookBriefList.add(bean);

            }

            bookBriefList.get(0).setLastChapterUrl(BIQUGE_URL + es.get(es.size() - 1).getElementsByTag("a").attr("href"));

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bookBriefList;
    }


    /**
     * @param url
     * @return
     * 获取章节正文数据
     */
    public static ChapterText getChapterText(String url){
        if (!url.endsWith(".html")){
            return null;
        }

        ChapterText chapterText = new ChapterText();
        try {
            Element element = Jsoup.connect(url).get().body().getElementsByClass("content_read").get(0);
            chapterText.setBookName(element.getElementsByClass("con_top").get(0).getElementsByTag("a").get(1).text());
            chapterText.setChapterName(element.getElementsByClass("bookname").get(0).getElementsByTag("h1").text());
            chapterText.setChapterText(element.getElementById("content").html().replace("&nbsp;", " ").replace("<br>",""));
            chapterText.setLastChapterUrl(BIQUGE_URL + element.getElementsByClass("bottem1").get(0).getElementsByTag("a").get(1).attr("href"));
            chapterText.setNextChapterUrl(BIQUGE_URL + element.getElementsByClass("bottem1").get(0).getElementsByTag("a").get(3).attr("href"));
            chapterText.setChapterUrl(url);
            //http://www.biquge.com.tw/8_8888/4344518.html
            chapterText.setChapterIndex(url.replace("//","/").split("/")[3].replace(".html",""));
            chapterText.setBookId(url.replace("//","/").split("/")[2]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return chapterText;
    }

    /**
     * @param urls      //bookdetailurl的String数组
     * @return
     * 获取订阅书籍列表数据
     */
    private List<BookEntity> getBookEntityList(String[] urls){
        List<BookEntity> bookEntityList = new ArrayList<>();

        try {
            for (int i = 0; i < urls.length; i++) {
                Elements elements = elements = Jsoup.connect(urls[i]).get().getElementsByClass("box_con");

                Element element1 = elements.get(0);

                BookEntity bookEntity = new BookEntity();
                bookEntity.setBookId(urls[i].replace("//","/").split("/")[2]);
                bookEntity.setBookName(element1.getElementById("info").getElementsByTag("h1").text());
                bookEntity.setLatestChapterName(element1.getElementById("info").getElementsByTag("p").get(3).text());
                bookEntity.setLastUpdateTime(element1.getElementById("info").getElementsByTag("p").get(2).text());
                bookEntity.setBookAuthor(element1.getElementById("info").getElementsByTag("p").get(0).text().substring(7));
                bookEntity.setChapterNum(elements.get(1).getElementById("list").getElementsByTag("dd").size() + "");
                bookEntity.setBookIconUrl(BIQUGE_URL + element1.getElementById("fmimg").getElementsByTag("img").attr("src"));
                bookEntity.setChapter(0 + "/" + bookEntity.getChapterNum());
                bookEntity.setFirstChapterIndex(elements.get(1).getElementById("list").getElementsByTag("dd").get(0).getElementsByTag("a").attr("href").split("/")[2].replace(".html",""));
                bookEntityList.add(bookEntity);

            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return bookEntityList;
    }

//    http://zhannei.baidu.com/cse/search?q=大主宰&p=5&entry=1&s=8353527289636145615&srt=dateModified&nsid=0
//    http://zhannei.baidu.com/cse/search?q=大主宰&p=5&s=8353527289636145615&srt=dft&nsid=0&entry=1

    public static final String SEARCH_MODE_DEF = "dft";
    public static final String SEARCH_MODE_DATE = "dateModified";
    /**
     * @param key      搜索关键词
     * @param mode      搜索模式
     * @return         bookBriefList
     */
    public static List<BookBrief> getSearchData(String key, int index, String mode){
        String url = "http://zhannei.baidu.com/cse/search?q=" + key + "&p=" + index + "&s=8353527289636145615&srt=" + mode + "&nsid=0&entry=1";
        List<BookBrief> bookBriefList = new ArrayList<>();
        try {
            Elements elements = Jsoup.connect(url).get().getElementById("results").getElementsByClass("result-list").get(0).getElementsByAttributeValue("class","result-item result-game-item");
            for (Element element : elements) {
                String iconUrl = element.getElementsByAttributeValue("class","result-game-item-pic").get(0).getElementsByTag("img").attr("src");
                String[] a = iconUrl.replace("//","/").split("/");
                if (a.length <= 6){
                    continue;
                }

                BookBrief bookBrief = new BookBrief();
                bookBrief.setIconUrl(iconUrl);
                Element e = element.getElementsByClass("result-game-item-detail").get(0);
                bookBrief.setBookUrl(BIQUGE_URL + a[5] + "_" + a[6]);
                bookBrief.setBookName(e.getElementsByAttributeValue("class","result-item-title result-game-item-title").get(0).getElementsByTag("h3").text());
                Elements es = e.getElementsByTag("p");
                if (es.size() == 4){
                    bookBrief.setBookBrief("暂无介绍");
                    bookBrief.setBookAuthor(es.get(0).text());
                    bookBrief.setBookType(es.get(1).text());
                    bookBrief.setLastTime(es.get(2).text());
                    bookBrief.setUpdateState(es.get(3).text());
                }else {
                    bookBrief.setBookBrief(es.get(0).text());
                    bookBrief.setBookAuthor(es.get(1).text());
                    bookBrief.setBookType(es.get(2).text());
                    bookBrief.setLastTime(es.get(3).text());
                    bookBrief.setUpdateState(es.get(4).text());
                }

                bookBriefList.add(bookBrief);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return bookBriefList;
    }

    private List<SortItem> getSortData(String url){
        List<SortItem> list = new ArrayList<>();

        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        SortItem item = new SortItem();
        item.setBookName("精品推荐");
        item.setViewType(0);
        list.add(item);

        Elements elements = document.getElementById("hotcontent").getElementsByClass("item");
        for (Element element : elements) {
            SortItem sortItem = new SortItem();
            Element element1 = element.getElementsByClass("image").get(0).getElementsByTag("a").get(0);
            sortItem.setBookUrl(element1.attr("href"));
//            sortItem.setBookIconUrl(element1.getElementsByTag("img").attr("data-cfsrc"));
            sortItem.setBookIconUrl(element1.getElementsByTag("img").attr("src"));
            sortItem.setBookName(element1.getElementsByTag("img").attr("alt"));
            sortItem.setBookAuthor(element.getElementsByTag("dl").get(0).getElementsByTag("span").text());
            sortItem.setBookInfo(element.getElementsByTag("dl").get(0).getElementsByTag("dd").text());
            sortItem.setViewType(1);
            list.add(sortItem);
        }

        SortItem item1 = new SortItem();
        item1.setBookName(document.getElementById("newscontent").getElementsByClass("l").get(0).getElementsByTag("h2").text());
        item1.setViewType(0);
        list.add(item1);

        Elements elements1 = document.getElementById("newscontent").getElementsByClass("l").get(0).getElementsByTag("li");
        for (Element element : elements1) {
            SortItem sortItem1 = new SortItem();
            Elements es = element.getElementsByTag("span");
            sortItem1.setBookName(es.get(0).getElementsByTag("a").text());
            sortItem1.setBookUrl(es.get(0).getElementsByTag("a").attr("href"));
            sortItem1.setChapterName(es.get(1).text());
            sortItem1.setChapterUrl(BIQUGE_URL + es.get(1).getElementsByTag("a").attr("href"));
            sortItem1.setBookAuthor(es.get(2).text());
            sortItem1.setViewType(2);
            list.add(sortItem1);
        }

        SortItem item2 = new SortItem();
        item2.setBookName(document.getElementById("newscontent").getElementsByClass("r").get(0).getElementsByTag("h2").text());
        item2.setViewType(0);
        list.add(item2);

        Elements elements2 = document.getElementById("newscontent").getElementsByClass("r").get(0).getElementsByTag("li");
        for (Element element : elements2) {
            SortItem sortItem2 = new SortItem();
            Elements es = element.getElementsByTag("span");
            sortItem2.setBookName(es.get(0).text());
            sortItem2.setBookUrl(es.get(0).getElementsByTag("a").attr("href"));
            sortItem2.setBookAuthor(es.get(1).text());
            sortItem2.setViewType(3);
            list.add(sortItem2);
        }
        return list;
    }



    private List<BookBrief> getSortHotData(String url){
        List<BookBrief> bookBriefList = new ArrayList<>();
        try {
            Elements elements = Jsoup.connect(url).get().getElementById("hotcontent").getElementsByClass("item");
            for (Element element : elements) {
                BookBrief bookBrief = new BookBrief();

                Element element1 = element.getElementsByClass("image").get(0).getElementsByTag("a").get(0);

                bookBrief.setBookUrl(element1.attr("href"));
                bookBrief.setIconUrl(element1.getElementsByTag("img").attr("data-cfsrc"));
                bookBrief.setBookName(element1.getElementsByTag("img").attr("alt"));
                bookBrief.setBookAuthor(element.getElementsByTag("dl").get(0).getElementsByTag("span").text());
                bookBrief.setBookBrief(element.getElementsByTag("dl").get(0).getElementsByTag("dd").text());

                bookBriefList.add(bookBrief);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bookBriefList;
    }

    private List<SortUpdateItem> getSortUpdateData(String url){
        List<SortUpdateItem> list = new ArrayList<>();
        try {
            Elements elements = Jsoup.connect(url).get().getElementById("newscontent").getElementsByClass("l").get(0).getElementsByTag("li");
            for (Element element : elements) {
                SortUpdateItem item = new SortUpdateItem();
                Elements es = element.getElementsByTag("span");
                item.setBookName(es.get(0).getElementsByTag("a").text());
                item.setBookUrl(es.get(0).getElementsByTag("a").attr("href"));
                item.setChapterName(es.get(1).getElementsByTag("a").text());
                item.setChapterUrl(BIQUGE_URL + es.get(1).getElementsByTag("a").attr("href"));
                item.setBookAuthor(es.get(2).text());
                list.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    private List<SortGoodItem> getSortGoodData(String url){
        List<SortGoodItem> list = new ArrayList<>();
        try {
            Elements elements = Jsoup.connect(url).get().getElementById("newscontent").getElementsByClass("r").get(0).getElementsByTag("li");
            for (Element element : elements) {
                SortGoodItem item = new SortGoodItem();
                Elements es = element.getElementsByTag("span");
                item.setBookName(es.get(0).text());
                item.setBookUrl(es.get(0).getElementsByTag("a").attr("href"));
                item.setBookAuthor(es.get(1).text());
                list.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return list;
    }
}
