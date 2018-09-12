package tv.jiaying.acms.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import tv.jiaying.acms.controller.pojos.ResultBean;

@Controller
@RequestMapping("/error")
@CrossOrigin
public class ErrorController {

    private static Logger logger = LoggerFactory.getLogger(ErrorController.class);

    @RequestMapping(value = "/500")
    @CrossOrigin
    @ResponseBody
    public ResultBean get500Error(){
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("/error/500");
//        modelAndView.addObject("status",500);
//        modelAndView.setView(new MappingJackson2JsonView());
//        return  modelAndView;
        return new ResultBean(500,"server error");

    }

    @RequestMapping(value = "/403")
    @CrossOrigin
    @ResponseBody
    public ResultBean get403Error(){

//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("/error/403");
//        modelAndView.addObject("status",403);
//        modelAndView.setView(new MappingJackson2JsonView());
//        return  modelAndView;
        return new ResultBean(403,"access denied");

    }

    @GetMapping(value = "/404")
    @CrossOrigin
    @ResponseBody
    public ResultBean get404Error(){

//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("/error/404");
//        modelAndView.addObject("status",404);
//        modelAndView.setView(new MappingJackson2JsonView());
//        return  modelAndView;

        return new ResultBean(404,"page not found");

    }
}
