package servlets.FamilyWeb;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import domain.FamilyWeb.Answer;
import domain.FamilyWeb.Contact;
import domain.FamilyWeb.Network;
import domain.FamilyWeb.Question;
import domain.FamilyWeb.Result;
import domain.FamilyWeb.Survey;
import domain.FamilyWeb.User;

/**
 * Servlet implementation class SurveyServlet
 */
public class SurveyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		User user = (User) req.getAttribute("user");
		RequestDispatcher reqDisp = null;	
		if(user != null){
			int client_id = Integer.parseInt((String) req.getSession().getAttribute("intervieweeC")); 
			int family_id = Integer.parseInt((String) req.getSession().getAttribute("intervieweeF"));
			Network newNetwork = new Network(new Date(new java.util.Date().getTime()), req.getParameter("general_comment"));
			Survey survey = (Survey) req.getSession().getAttribute("survey");
			ArrayList<Contact> contacts = (ArrayList<Contact>) req.getSession().getAttribute("contacts");
			newNetwork.setTheSurvey(survey);
			for(Contact c : contacts){
				ArrayList<Result> results = new ArrayList<Result>();
				for(Question question : survey.getQuestions()){
					int answerID = Integer.parseInt(req.getParameter(question.getQuestion_id()+":"+c.getContact_id()));
					Answer answer = null;
					for(Answer a : question.getTheAnswers()){
						if(a.getAnswer_id()==answerID)
							answer = a;
					}
					results.add(new Result(question,answer));
				}
				c.setMyResults(results);
			}
			newNetwork.setContacts(contacts);
			user.getDbController().addNetwork(newNetwork, client_id, family_id);
		}else{
			req.setAttribute("message", "Er is iets onverwachts gelopen, probeer opnieuw in te loggen.");
			req.setAttribute("messageType", "error");
			reqDisp = req.getRequestDispatcher("/login.jsp");
		}
		reqDisp.forward(req, resp);
	}
}