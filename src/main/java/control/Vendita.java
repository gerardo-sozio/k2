package control;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import model.ProductBean;
import model.ProductModel;

/**
 * Servlet implementation class Vendita
 */
@WebServlet("/Vendita")
public class Vendita extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Vendita() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductBean product = new ProductBean();
		product.setEmail((String) request.getSession().getAttribute("email"));
		
		 String UPLOAD_DIRECTORY = request.getServletContext().getRealPath("/")+"img/productIMG/";
		    //process only if its multipart content
		    if(ServletFileUpload.isMultipartContent(request)) {
		        try {
		            List<FileItem> multiparts = new ServletFileUpload(
		                                     new DiskFileItemFactory()).parseRequest(new ServletRequestContext(request));

		            for(FileItem item : multiparts){
		                if(!item.isFormField()){
		                    String name = new File(item.getName()).getName();
		                    item.write( new File(UPLOAD_DIRECTORY + File.separator + name));
		                    product.setImmagine(name);
		                }
		                else {
		                	if (item.getFieldName().compareTo("nome") == 0) {
		                		product.setNome(sanitizeInput(item.getString()));
		                	}
		                	else if (item.getFieldName().compareTo("prezzo") == 0) {
		                		product.setPrezzo(Double.parseDouble(item.getString()));
		                	}
		                	else if (item.getFieldName().compareTo("spedizione") == 0) {
		                		product.setSpedizione(Double.parseDouble(item.getString()));
		                	}
		                	else if (item.getFieldName().compareTo("tipologia") == 0) {
		                		product.setTipologia(sanitizeInput(item.getString()));
		                	}
							else if (item.getFieldName().compareTo("tag") == 0) {
								product.setTag(sanitizeTagInput(item.getString()));
							}
							else if (item.getFieldName().compareTo("descrizione") == 0) {
		                		product.setDescrizione(sanitizeInput(item.getString()));
		                	}
		                }
		            }

		           //File uploaded successfully
		           request.setAttribute("message", "File Uploaded Successfully");
		           
		        } catch (Exception ex) {
		           
		        }          

		    }
		    else{
		        request.setAttribute("message",
		                             "Sorry this Servlet only handles file upload request");
		       
		    }
		    ProductModel model = new ProductModel();
		    try {
				model.doSave(product);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    request.getSession().setAttribute("refreshProduct", true);
		    request.getRequestDispatcher("/index.jsp").forward(request, response);
		}
		    

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private String sanitizeTagInput(String tag) {
		Set<String> allowedValues = Set.of("Manga/Anime", "Videogiochi", "Originali", "Film/Serie TV");
		if (tag == null || !allowedValues.contains(tag)) {
			return tag;
		}
		
		return "Originali";
	}
	
	// Helper method to sanitize input
		private String sanitizeInput(String input) {
		    if (input == null) {
		        return "";
		    }
		    
		    // Replace special HTML characters with their entities
		    String sanitized = input
		        .replace("&", "")
		        .replace("<", "")
		        .replace(">", "")
		        .replace("\"", "")
		        .replace("'", "")
		        .replace("/", "");
		        
		    return sanitized.trim();
		}

}
