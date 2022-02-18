package compute;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.javabip.annotations.ComponentType;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@ComponentType(initial = "init", name = "compute.Compute")
//@WebServlet(name = "Compute Server", urlPatterns = "/")
public class Compute extends HttpServlet {

	LimitCounter counter = new LimitCounter();
	int limit = 5;
//	int rid = 0;

//	public void increment() {
//		rid++;
//	}

	public int randomNumber() {
		Random rand = new Random();
		int n = rand.nextInt(100);
		n += 1;
		return n;
	}

	private static final long serialVersionUID = -4751096228274971485L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		List<String> parameterNames = new ArrayList<String>(request.getParameterMap().keySet());

		if (!parameterNames.isEmpty()) {
			String firstParam = parameterNames.get(0);
			String firstValue = request.getParameter(firstParam);

//			if (parameterNames.get(0) == firstParam && "checkLimit".equals(firstValue)) {
			if ("req".equals(firstParam) && "checkLimit".equals(firstValue)) {
				JsonObject limitInfo = new JsonObject();
				limitInfo.addProperty("counter", counter.getCounter());
				limitInfo.addProperty("requestLimit", limit);

				String reqJsonString = new Gson().toJson(limitInfo);

				PrintWriter out = response.getWriter();
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				out.print(reqJsonString);
				out.flush();
			}
			if ("req".equals(firstParam) && "reset".equals(firstValue)) {
				counter.reset();
			}

		} else {

			int n = randomNumber();
			System.out.println("Random number: " + n);

//		//Limit counter: if the limit is reach, the counter will be reset. 
//		 if (counter == null)
//		 {
//			 counter = new LimitCounter();
//			 counter.increment();
//		 }
//		 else if (counter != null && counter.getCounter() < limit)
//		 {
//			 counter.increment();
//		 }
//		 else
//		 {
//			 counter.reset();
//			 System.out.println("The limit is reached --> counter reset!");
//		 }

			// Limit counter: if the limit is reach, the counter will be reset.
			if (counter.getCounter() < limit) {
				counter.increment();
			} else {
				counter.reset();
				System.out.println("The limit is reached --> counter reset!");
			}

			Request req2 = new Request(n, request.getRequestURL().toString(), counter.getCounter(), limit);
//		 	increment();
//		 	Request req2 = new Request( rid, n, request.getRequestURL().toString(), counter.getCounter(), limit);
			DBUtil.create(req2);

			// Json format
			String reqJsonString = new Gson().toJson(req2);

			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out.print(reqJsonString);
			out.flush();

		}

	}

	@Override
	public void init() throws ServletException {
		System.out.println("Servlet " + this.getServletName() + " has started");
	}

	@Override
	public void destroy() {
		System.out.println("Servlet " + this.getServletName() + " has stopped");
	}

}
