package com.poprosturonin.sites.ninegag;

import com.poprosturonin.data.Page;
import com.poprosturonin.exceptions.PageIsEmptyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The coding love
 */
@RestController
@RequestMapping(value = "/9gagnsfw")
public class NinegagNSFWController {
    final static String ROOT_URL = "https://9gag.com/v1/group-posts/group/nsfw/type/hot";
    final static String ID_URL_PART = "?c=10&after=";

    @Autowired
    private NinegagNSFWPageScrapper ninegagNSFWPageScrapper;

    @RequestMapping(value = "")
    @ResponseBody
    public Page readerPage() {
        return ninegagNSFWPageScrapper.scrapPage(ROOT_URL);
    }

    @RequestMapping(value = "/page/{id}")
    @ResponseBody
    public Page readerPage(@PathVariable String id) {
        if (id.length() <= 4) //There is no 9gag id that has 4 letters
            throw new PageIsEmptyException();

        return ninegagNSFWPageScrapper.scrapPage(ROOT_URL + ID_URL_PART + id);
    }
}
