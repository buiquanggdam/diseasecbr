/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.diseasesearchcbr;

import de.dfki.mycbr.core.ICaseBase;
import de.dfki.mycbr.core.Project;
import de.dfki.mycbr.core.casebase.Instance;
import de.dfki.mycbr.core.model.AttributeDesc;
import de.dfki.mycbr.core.model.Concept;
import de.dfki.mycbr.core.model.SymbolDesc;
import de.dfki.mycbr.core.retrieval.Retrieval;
import de.dfki.mycbr.core.similarity.Similarity;
import de.dfki.mycbr.util.Pair;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Quang Đảm
 */
public class DiseaseSearchResults extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Project prj;
    private ICaseBase defaultCB;
    private Concept concept;
    private int casesCount = 1;

    public DiseaseSearchResults() {
        CBRInit cbrinit = new CBRInit();
        prj = cbrinit.createProjectFromPRJ();
        defaultCB = prj.getCB(cbrinit.getCaseBase());
        concept = prj.getConceptByID(CBRInit.getConceptName());
    }

    private ArrayList<Hashtable<String, String>> parseCSQuery(String face1, String eyes1, String mouth1, String tongue1, String jaw1, String age1, String speak1) {

        int totalCasesCount = defaultCB.getCases().size();

        if (casesCount > totalCasesCount) {
            casesCount = totalCasesCount;
        }

        SymbolDesc face = (SymbolDesc) concept.getAllAttributeDescs().get("Face");
        SymbolDesc eye = (SymbolDesc) concept.getAllAttributeDescs().get("Eyes");
        SymbolDesc mouth = (SymbolDesc) concept.getAllAttributeDescs().get("Mouth");
        SymbolDesc tongue = (SymbolDesc) concept.getAllAttributeDescs().get("Tongue");
        SymbolDesc jaw = (SymbolDesc) concept.getAllAttributeDescs().get("Jaw");
        SymbolDesc age = (SymbolDesc) concept.getAllAttributeDescs().get("Age");
        SymbolDesc speak = (SymbolDesc) concept.getAllAttributeDescs().get("Speak");
        SymbolDesc disease = (SymbolDesc) concept.getAllAttributeDescs().get("Disease");

        // Khởi tạo và xác định method Retrieval để tính toán độ tương đồng (sắp xếp giảm dần theo độ tương đồng)
        Retrieval disRetrieval = new Retrieval(concept, defaultCB);
        disRetrieval.setRetrievalMethod(Retrieval.RetrievalMethod.RETRIEVE_SORTED);

        Instance queryInstance = disRetrieval.getQueryInstance();

        try {
            queryInstance.addAttribute(face, face.getAttribute(face1));
            queryInstance.addAttribute(eye, eye.getAttribute(eyes1));
            queryInstance.addAttribute(mouth, mouth.getAttribute(mouth1));
            queryInstance.addAttribute(tongue, tongue.getAttribute(tongue1));
            queryInstance.addAttribute(jaw, jaw.getAttribute(jaw1));
            queryInstance.addAttribute(age, age.getAttribute(age1));
            queryInstance.addAttribute(speak, speak.getAttribute(speak1));
        } catch (Exception ex) {
            Logger.getLogger(DiseaseSearchResults.class.getName()).log(Level.SEVERE, null, ex);
        }

        disRetrieval.start();
        // Lấy kết quả sau khi tính toán độ tương đồng so với các case mẫu

        List<Pair<Instance, Similarity>> resultList = disRetrieval.getResult();

        for (int i = 0; i < resultList.size(); i++) {
            System.out.println(resultList.get(i).toString());
        }

        // Thêm case mới vào cases-base
        ArrayList<Hashtable<String, String>> resultTable;
        if (resultList.size() > 0) {
            resultTable = new ArrayList<>();
            for (int i = 0; i < casesCount; i++) {
                resultTable.add(getAttributes(resultList.get(i), prj.getConceptByID(CBRInit.getConceptName())));
                System.out.println("liste " + resultTable.get(i).toString());
                System.out.println("tên bệnh: " + resultTable.get(i).get("Disease"));
            }
        } else {
            resultTable = null;
        }

        return resultTable;
//        Tính toán độ tương đồng
//        disRetrieval.start();

////      Lấy kết quả sau khi tính toán độ tương đồng so với các case mẫu
//        List<Pair<Instance, Similarity>> resultList = disRetrieval.getResult();
//        resultList.get(0).getFirst().getName();
//
//        for (int i = 0; i < resultList.size(); i++) {
//            System.out.println(resultList.get(i).toString());
//        }
//        // Thêm case mới vào cases-base
//        ArrayList<Hashtable<String, String>> resultTable;
//        if (resultList.size() > 0) {
//            resultTable = new ArrayList<>();
//
//            resultTable.add(getAttributes(resultList.get(0), prj.getConceptByID(CBRInit.getConceptName())));
//            System.out.println("liste " + resultTable.get(0).toString());
//            String newName = "Benh #" + String.valueOf(prj.getCurrentNumberOfCases());
//            try {
//                Instance newInstance = concept.addInstance(newName);
//                newInstance.addAttribute(face, face.getAttribute(face1));
//                newInstance.addAttribute(eye, eye.getAttribute(eyes1));
//                newInstance.addAttribute(mouth, mouth.getAttribute(mouth1));
//                newInstance.addAttribute(tongue, tongue.getAttribute(tongue1));
//                newInstance.addAttribute(jaw, jaw.getAttribute(jaw1));
//                newInstance.addAttribute(age, age.getAttribute(age1));
//                newInstance.addAttribute(speak, speak.getAttribute(speak1));
//                newInstance.addAttribute(disease, resultTable.get(0).get("Disease"));
//                defaultCB.addCase(newInstance);
//                prj.save();
//
//            } catch (Exception ex) {
//                Logger.getLogger(DiseaseSearchResults.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            resultList.get(0).getFirst().getName();
//            System.out.println("Test" + resultTable.get(0).get("Disease"));
//
//            try {
//                queryInstance.addAttribute(disease, resultTable.get(0).get("Disease"));
//
//                defaultCB.addCase(queryInstance);
//                System.out.println("Check" + prj.getCurrentNumberOfCases());
//            } catch (ParseException ex) {
//                Logger.getLogger(DiseaseSearchResults.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        } else {
//            resultTable = null;
//        }
//
//        return resultTable;
    }

//    Hashtable chứa tên các Thuộc tính của các case
//    cùng độ tương đồng của case được đưa vào so với các case mẫu
    private static Hashtable<String, String> getAttributes(Pair<Instance, Similarity> r, Concept concept) {

        Hashtable<String, String> resTable = new Hashtable<>();
        ArrayList<String> categories = getCategories(r);
        // Thêm độ tương đồng của case
        resTable.put("Sim", String.valueOf(r.getSecond().getValue()));
        categories.forEach(category -> {
            // Thêm tên Thuộc tính và giá trị của nó vào Hashtable
            resTable.put(category,
                    r.getFirst().getAttForDesc(concept.getAllAttributeDescs().get(category))
                            .getValueAsString());
        });
        return resTable;
    }

    // trả về một Arraylist chứa tất cả các mục của concept
    private static ArrayList<String> getCategories(Pair<Instance, Similarity> r) {

        ArrayList<String> categories = new ArrayList<>();

        // Đọc tất cả các thuộc tính của một concept
        Set<AttributeDesc> categoryList = r.getFirst().getAttributes().keySet();
        for (AttributeDesc category : categoryList) {
            if (category != null) {
                categories.add(category.getName());                     //Thêm các thuộc tính vào Arraylist
            }
        }
        return categories;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        ArrayList<Hashtable<String, String>> queryResult = parseCSQuery(
                request.getParameter("face"),
                request.getParameter("eyes"),
                request.getParameter("mouth"),
                request.getParameter("tongue"),
                request.getParameter("jaw"),
                request.getParameter("age"),
                request.getParameter("speak"));

        // Kết quả đầu ra
        try ( PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Kết quả chẩn đoán</title>");
            out.println("<link href='style.css' rel='stylesheet' type='text/css' />");
            out.println("</head>");
            out.println("<body>");
            out.println(
                    "<div id='nav'><p>Chẩn đoán bệnh <span class='page-title'>/ Kết quả</span></p></div>");

            out.println("<h3 id='new-query'><a href='DiseaseSearchQuery'>Thử lại</a></h3>");

            out.println("<div id='search-results'>");
            queryResult.stream().map((queryRes) -> {
                Hashtable<String, String> singleResult;
                singleResult = queryRes;
                return singleResult;
            }).forEach((singleResult) -> {

                // Đầu ra kết quả là các bệnh
                out.println("<div class = 'result'style:'line-height:5vh;>"
                        + "Hệ thống cho rằng bệnh mà người bệnh mắc phải là: " + singleResult.get("Disease")
                        + "</div>");

                // Đầu ra giới thiệu và lời khuyên
                String diseaseName = singleResult.get("Disease");
                if (diseaseName.equals("Rối loạn vận động chậm phát (Tardive dyskinesia)")) {
                    out.println("<div id = 'description'>\n"
                            + "        Các rối loạn vận động chậm phát (TDs) là những chuyển động không \n"
                            + "        kiểm soát được của lưỡi môi, mặt, thân và các chi. Bệnh thường xảy ra ở những \n"
                            + "        người đang dùng các thuốc kháng acid dopaminergic dài hạn. Bệnh nhân tâm thần phân liệt,\n"
                            + "        rối loạn tâm thần phân liệt, rối loạn lưỡng cực đã được điều trị bằng thuốc chống loạn thần\n"
                            + "        trong thời gian dài thường mắt phải các chứng rối loạn vận động chậm, nhưng bệnh này có thể\n"
                            + "        xuất hiện ở những bệnh nhân khác. Tình trạng sức khoẻ này rất phổ biến, bệnh có thể ảnh thướng\n"
                            + "        đến bệnh nhân ở mọi lứa tuổi.Tham khảo thêm tại "
                            + "        <a href=''>đây</a>"
                            + "    </div>\n"
                            + "    <div style=\"font-weight: bold;\">\n"
                            + "        Lời khuyên: Người bệnh cần ăn uống điều độ, tăng cường tập thể dục, cố gắng không để bệnh ảnh hưởng đến cuộc sống thường ngày\n"
                            + "    </div>");
                } else if (diseaseName.equals("Bình thường (không có bệnh)")) {
                    out.println("<div id = 'description'>\n"
                            + "        Sức khoẻ của bạn hoàn toàn bình thường\n"
                            + "    </div>\n"
                            + "    <div style=\"font-weight: bold;\">\n"
                            + "        Lời khuyên: Uống nhiều nước, hạn chế đồ ngọt, tăng cường tập thể dục\n"
                            + "    </div>");
                } else if (diseaseName.equals("Co giật nửa mặt (Hemifacial Spasm)")) {
                    out.println("<div id = 'description'>\n"
                            + "        Co giật nửa mặt (Hemifacial Spasm) là một tình trạng co giật không tự ý, \n"
                            + "        ngắt quãng của các nhóm cơ chi phối dây thần kinh VII ở một bên mặt và không gây đau. \n"
                            + "        Bệnh tuy không gây đau và đe doạ tình mạng, nhưng gây ảnh hưởng rất lớn đến tâm lý và tinh thần của người bệnh, \n"
                            + "        từ đó làm cản trở trong giao tiếp xã hội và ảnh hưởng đến sinh hoạt và công việc. \n"
                            + "        Thường xuất hiện ở các bệnh nhân từ 40 tuổi đến 60 tuổi.Tham khảo thêm tại <a href=''>đây</a>\n"
                            + "    </div>\n"
                            + "    <div style=\"font-weight: bold;\">\n"
                            + "        Lời khuyên: Người bệnh cần đi cấp cứu nếu người bệnh bị co giật quá 5 phút, tập thể dục thường xuyên, ăn uống điều độ.\n"
                            + "    </div>");
                } else if (diseaseName.equals("Co thắt mí mắt (Blepharospasm)")) {
                    out.println("<div id = 'description'>\n"
                            + "        Co thắt mí mắt là một rối loạn thần kinh gây ra các cử động cơ không kiểm soát được \n"
                            + "        khiến mí mắt đóng lại hoặc khó mở (loạn trương lực cơ). Điều này có thể ảnh hưởng đến \n"
                            + "        khả năng nhìn của bệnh nhân. Độ tuổi trung bình của các bệnh nhân là từ 40 đến 60 tuổi. "
                            + "        Tham khảo thêm tại <a href=''>đây</a>\n"
                            + "    </div>\n"
                            + "    <div style=\"font-weight: bold;\">\n"
                            + "        Lời khuyên: Người bệnh cần đi cấp cứu nếu mí mắt co giật quá 5 phút, tập thể dục thường xuyên, ăn uống điều độ, hạn chế hút thuốc.\n"
                            + "    </div>");
                } else if (diseaseName.equals("Hội chứng Meige")) {
                    out.println("<div id = 'description'>\n"
                            + "        Hội chứng Meige là một dạng hiếm gặp của chứng loạn trương lực cơ, \n"
                            + "        một chứng rối loạn hệ thần kinh, trong đó một người thường xuyên bị co thắt \n"
                            + "        cưỡng bức của mắt, hàm, lưỡi và các cơ mặt dưới. Các cơn co thắt có thể giống như \n"
                            + "        cảm giác bị đâm, tương tự như bị điện giật. Vì các cử động nằm ngoài khả năng \n"
                            + "        kiểm soát của người bị bệnh, chúng có thể gây ra sự khó khăn trong các tình huống xã hội. \n"
                            + "        Độ tuổi dễ mắc bệnh là từ 40 tuổi đến 60 tuổi. Tham khảo thêm tại <a href=''>đây</a>\n"
                            + "    </div>\n"
                            + "    <div style=\"font-weight: bold;\">\n"
                            + "        Lời khuyên: Người bệnh cần thông báo tình trạng bản thân trước với mọi người, tập thể dục thường xuyên, ăn uống điều độ, hạn chế hút thuốc, uống rượu.\n"
                            + "    </div>");
                } else if (diseaseName.equals("Hội trứng Tourette")) {
                    out.println("<div id = 'description'>\n"
                            + "        Hội trứng Tourette là một chứng rối loạn liên quan đến các chuyển động \n"
                            + "        lặp đi lặp lại hoặc âm thanh không mong muốn (tics) không thể dễ dàng kiểm soát được. \n"
                            + "        Ví dụ, bạn có thể liên tục chớp mắt, nhún vai hoặc thốt ra những âm thanh bất thường hoặc \n"
                            + "        những từ xúc phạm. Tics thường xuất hiện trong độ tuổi từ 2 đến 15, với độ tuổi trung bình \n"
                            + "        là khoảng 6 tuổi. Nam giới có nguy cơ mắc hội chứng Tourette cao hơn nữ giới khoảng 3-4 lần. "
                            + "        Tham khảo thêm tại <a href=''>đây</a>\n"
                            + "    </div>\n"
                            + "    <div style=\"font-weight: bold;\">\n"
                            + "        Lời khuyên: Người bệnh cần thông báo tình trạng bản thân trước với mọi người, tập thể dục thường xuyên, ăn uống điều độ, hạn chế hút thuốc, uống rượu.\n"
                            + "    </div>");

                } else if (diseaseName.equals("Liệt dây thần kinh mặt Bell’s Palsy")) {
                    out.println("<div id = 'description'>\n"
                            + "        Liệt dây thần kinh mặt Bell's Palsy là một tình trạng gây ra tình trạng yếu tạm thời hoặc tê liệt các cơ ở mặt. \n"
                            + "        Nó có thể xảy ra khi dây thần kinh điều khiển cơ mặt của bạn bị viêm, sưng hoặc bị nén. Mọi độ tuổi đều có thể \n"
                            + "        mắc bệnh nên cần chú ý.\n Tham khảo thêm tại "
                            + "        <a href='https://www.aci.health.nsw.gov.au/__data/assets/pdf_file/0004/350617/VIETNAMESE_Bells_Palsy_ED_Patient_Factsheet.pdf'>đây</a>"
                            + "    </div>\n"
                            + "    <div style=\"font-weight: bold;\">\n"
                            + "        Lời khuyên: Người bệnh cần thông báo tình trạng bản thân trước với mọi người, tập thể dục thường xuyên, ăn uống điều độ, hạn chế hút thuốc, uống rượu, gặp bác sĩ tâm lý nếu cần.\n"
                            + "    </div>");
                } else if (diseaseName.equals("Rối loạn TIC")) {
                    out.println("<div id = 'description'>\n"
                            + "        Rối loạn TIC là tình trạng co thắt không kiểm soát ở mặt như mắt nhấp nháy hoặc nhăn mũi. \n"
                            + "        Chúng cũng có thể được gọi là co thắt bắt chước. Mặc dù rối loạn TIC thường tự phát, \n"
                            + "        chúng có thể bị ức chế tạm thời. Rối loạn TIC có thể gặp phải ở mọi độ tuổi. "
                            + "        Tham khảo thêm tại <a href=''>đây</a>\n"
                            + "    </div>\n"
                            + "    <div style=\"font-weight: bold;\">\n"
                            + "        Lời khuyên: Người bệnh cần thông báo tình trạng bản thân trước với mọi người, tập thể dục thường xuyên, ăn uống điều độ, hạn chế hút thuốc, uống rượu, gặp bác sĩ tâm lý nếu cần.\n"
                            + "    </div>");
                } else if (diseaseName.equals(" Đau dây thần kinh sinh ba")) {
                    out.println("<div id = 'description'>\n"
                            + "        Đau dây thần kinh sinh ba là một chứng bệnh hiếm gặp. Các sợi dây thần kinh cảm giác và \n"
                            + "        của dây thần kinh bị tổn thương nên khi một kích thích xuất hiện sẽ hình thành nên một xung động đau. \n"
                            + "        Những kích thích kéo dài, lặp lại nhiều lần tạo nên một vùng hưng phấn ở vỏ não làm người bệnh có cảm giác \n"
                            + "        đau thường xuyên, liên tục và dữ dội. Đau dây thần kinh sinh ba là một chứng bệnh mãn tính, kéo dài \n"
                            + "        nhiều ngày đến nhiều tháng. Các bệnh nhân thường là nhóm người cao tuổi từ 60 tuổi trở lên. "
                            + "        Tham khảo thêm tại <a href=''>đây</a>\n"
                            + "    </div>\n"
                            + "    <div style=\"font-weight: bold;\">\n"
                            + "        Lời khuyên: Người bệnh cần thông báo tình trạng bản thân trước với mọi người, tập thể dục thường xuyên, ăn uống điều độ, hạn chế hút thuốc, uống rượu, gặp bác sĩ tâm lý nếu cần.\n"
                            + "    </div>");
                } else if (diseaseName.equals("Đột quỵ")) {
                    out.println("<div id = 'description'>\n"
                            + "        Đột quỵ xảy ra khi nguồn cung cấp máu đến một phần não của bạn bị gián đoạn hoặc giảm, \n"
                            + "        ngăn cản các mô não nhận được oxy và chất dinh dưỡng. Tế bào não bắt đầu chết trong vài phút. \n"
                            + "        Đột quỵ là một trường hợp cấp cứu y tế và điều trị kịp thời là rất quan trọng. \n"
                            + "        Hành động sớm có thể làm giảm tổn thương não và các biến chứng khác. Bệnh đột quỵ xuất hiện ở \n"
                            + "        mọi độ tuổi nên mọi người cần chú ý. Tham khảo thêm tại <a href=''>đây</a>\n"
                            + "    </div>\n"
                            + "    <div style=\"font-weight: bold;\">\n"
                            + "        Lời khuyên: Người bệnh cần kiểm tra sức khỏe định kì, tập thể dục thường xuyên, ăn uống điều độ, hạn chế hút thuốc, uống rượu, gặp bác sĩ tâm lý nếu cần.\n"
                            + "    </div>");
                }
            });
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
