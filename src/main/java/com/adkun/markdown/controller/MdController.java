package com.adkun.markdown.controller;

import com.adkun.markdown.common.ResponseModel;
import com.adkun.markdown.markdown.MdParser;
import com.adkun.markdown.markdown.MdTools;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "${adkun.web.path}", allowedHeaders = "*", allowCredentials = "true")
public class MdController {

    @GetMapping("/parse")
    public ResponseModel mdParseHtml(String md) {
        List<String> list = MdTools.splitStr(md);
        MdParser parser = new MdParser(list);
        String res = parser.parseMdToHtml();
        return new ResponseModel(res);
    }
}
