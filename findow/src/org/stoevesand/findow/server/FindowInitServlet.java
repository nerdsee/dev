package org.stoevesand.findow.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stoevesand.findow.jobs.JobManager;
import org.stoevesand.findow.persistence.PersistanceManager;
import org.stoevesand.findow.provider.figo.FigoBankingAPI;

public class FindowInitServlet extends HttpServlet {

    private Logger log = LoggerFactory.getLogger(FigoBankingAPI.class);

    public void init() throws ServletException {

        log.info("FINDOW INIT. start.");

        JobManager jm = JobManager.getInstance();
        PersistanceManager pm = PersistanceManager.getInstance();

        log.info("FINDOW INIT. done.");

    }

}
