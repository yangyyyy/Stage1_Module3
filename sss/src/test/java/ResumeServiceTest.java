import com.lagou.edu.pojo.Resume;
import com.lagou.edu.service.ResumeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class ResumeServiceTest {

    @Autowired
    private ResumeService resumeService;

    @Test
    public void testResumeService(){
        List<Resume> resumes = resumeService.queryAll();
        for (Resume resume : resumes) {
            System.out.println(resume);
        }
    }
}
