package com.chengxusheji.action;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import com.opensymphony.xwork2.ActionContext;
import com.chengxusheji.dao.ChapterDAO;
import com.chengxusheji.domain.Chapter;
import com.chengxusheji.utils.FileTypeException;
import com.chengxusheji.utils.ExportExcelUtil;

@Controller @Scope("prototype")
public class ChapterAction extends BaseAction {

    /*当前第几页*/
    private int currentPage;
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    public int getCurrentPage() {
        return currentPage;
    }

    /*一共多少页*/
    private int totalPage;
    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
    public int getTotalPage() {
        return totalPage;
    }

    private int id;
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    /*当前查询的总记录数目*/
    private int recordNumber;
    public void setRecordNumber(int recordNumber) {
        this.recordNumber = recordNumber;
    }
    public int getRecordNumber() {
        return recordNumber;
    }

    /*业务层对象*/
    @Resource ChapterDAO chapterDAO;

    /*待操作的Chapter对象*/
    private Chapter chapter;
    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }
    public Chapter getChapter() {
        return this.chapter;
    }

    /*跳转到添加Chapter视图*/
    public String AddView() {
        ActionContext ctx = ActionContext.getContext();
        return "add_view";
    }

    /*添加Chapter信息*/
    @SuppressWarnings("deprecation")
    public String AddChapter() {
        ActionContext ctx = ActionContext.getContext();
        try {
            chapterDAO.AddChapter(chapter);
            ctx.put("message",  java.net.URLEncoder.encode("Chapter添加成功!"));
            return "add_success";
        } catch(FileTypeException ex) {
        	ctx.put("error",  java.net.URLEncoder.encode("图片文件格式不对!"));
            return "error";
        } catch (Exception e) {
            e.printStackTrace();
            ctx.put("error",  java.net.URLEncoder.encode("Chapter添加失败!"));
            return "error";
        }
    }

    /*查询Chapter信息*/
    public String QueryChapter() {
        if(currentPage == 0) currentPage = 1;
        List<Chapter> chapterList = chapterDAO.QueryChapterInfo(currentPage);
        /*计算总的页数和总的记录数*/
        chapterDAO.CalculateTotalPageAndRecordNumber();
        /*获取到总的页码数目*/
        totalPage = chapterDAO.getTotalPage();
        /*当前查询条件下总记录数*/
        recordNumber = chapterDAO.getRecordNumber();
        ActionContext ctx = ActionContext.getContext();
        ctx.put("chapterList",  chapterList);
        ctx.put("totalPage", totalPage);
        ctx.put("recordNumber", recordNumber);
        ctx.put("currentPage", currentPage);
        return "query_view";
    }

    /*后台导出到excel*/
    public String QueryChapterOutputToExcel() { 
        List<Chapter> chapterList = chapterDAO.QueryChapterInfo();
        ExportExcelUtil ex = new ExportExcelUtil();
        String title = "Chapter信息记录"; 
        String[] headers = { "记录编号","章标题","添加时间"};
        List<String[]> dataset = new ArrayList<String[]>(); 
        for(int i=0;i<chapterList.size();i++) {
        	Chapter chapter = chapterList.get(i); 
        	dataset.add(new String[]{chapter.getId() + "",chapter.getTitle(),chapter.getAddTime()});
        }
        /*
        OutputStream out = null;
		try {
			out = new FileOutputStream("C://output.xls");
			ex.exportExcel(title,headers, dataset, out);
		    out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		HttpServletResponse response = null;//创建一个HttpServletResponse对象 
		OutputStream out = null;//创建一个输出流对象 
		try { 
			response = ServletActionContext.getResponse();//初始化HttpServletResponse对象 
			out = response.getOutputStream();//
			response.setHeader("Content-disposition","attachment; filename="+"Chapter.xls");//filename是下载的xls的名，建议最好用英文 
			response.setContentType("application/msexcel;charset=UTF-8");//设置类型 
			response.setHeader("Pragma","No-cache");//设置头 
			response.setHeader("Cache-Control","no-cache");//设置头 
			response.setDateHeader("Expires", 0);//设置日期头  
			String rootPath = ServletActionContext.getServletContext().getRealPath("/");
			ex.exportExcel(rootPath,title,headers, dataset, out);
			out.flush();
		} catch (IOException e) { 
			e.printStackTrace(); 
		}finally{
			try{
				if(out!=null){ 
					out.close(); 
				}
			}catch(IOException e){ 
				e.printStackTrace(); 
			} 
		}
		return null;
    }
    /*前台查询Chapter信息*/
    public String FrontQueryChapter() {
        if(currentPage == 0) currentPage = 1;
        List<Chapter> chapterList = chapterDAO.QueryChapterInfo(currentPage);
        /*计算总的页数和总的记录数*/
        chapterDAO.CalculateTotalPageAndRecordNumber();
        /*获取到总的页码数目*/
        totalPage = chapterDAO.getTotalPage();
        /*当前查询条件下总记录数*/
        recordNumber = chapterDAO.getRecordNumber();
        ActionContext ctx = ActionContext.getContext();
        ctx.put("chapterList",  chapterList);
        ctx.put("totalPage", totalPage);
        ctx.put("recordNumber", recordNumber);
        ctx.put("currentPage", currentPage);
        return "front_query_view";
    }

    /*查询要修改的Chapter信息*/
    public String ModifyChapterQuery() {
        ActionContext ctx = ActionContext.getContext();
        /*根据主键id获取Chapter对象*/
        Chapter chapter = chapterDAO.GetChapterById(id);

        ctx.put("chapter",  chapter);
        return "modify_view";
    }

    /*查询要修改的Chapter信息*/
    public String FrontShowChapterQuery() {
        ActionContext ctx = ActionContext.getContext();
        /*根据主键id获取Chapter对象*/
        Chapter chapter = chapterDAO.GetChapterById(id);

        ctx.put("chapter",  chapter);
        return "front_show_view";
    }

    /*更新修改Chapter信息*/
    public String ModifyChapter() {
        ActionContext ctx = ActionContext.getContext();
        try {
            chapterDAO.UpdateChapter(chapter);
            ctx.put("message",  java.net.URLEncoder.encode("Chapter信息更新成功!"));
            return "modify_success";
        } catch (Exception e) {
            e.printStackTrace();
            ctx.put("error",  java.net.URLEncoder.encode("Chapter信息更新失败!"));
            return "error";
       }
   }

    /*删除Chapter信息*/
    public String DeleteChapter() {
        ActionContext ctx = ActionContext.getContext();
        try { 
            chapterDAO.DeleteChapter(id);
            ctx.put("message",  java.net.URLEncoder.encode("Chapter删除成功!"));
            return "delete_success";
        } catch (Exception e) { 
            e.printStackTrace();
            ctx.put("error",  java.net.URLEncoder.encode("Chapter删除失败!"));
            return "error";
        }
    }

}
