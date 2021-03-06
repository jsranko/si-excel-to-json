package de.sranko_informatik.siexceltojsongui;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class GuiController {

    @Autowired
    FileService fileService;

    @GetMapping("/")
    public String index(@RequestParam(name = "callback") String callbackUrl,
                        Model model) {
        model.addAttribute("callbackUrl", callbackUrl);
        return "upload";
    }

    @GetMapping("/uploaded")
    public String uploaded() {
        return "uploaded";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam(name = "file") MultipartFile file,
                             @RequestParam(name = "callback") String callbackUrl,
                             @RequestParam(name = "clientInfo") String clientInfo,
                             RedirectAttributes redirectAttributes) {

        JSONObject callbackData = null;
        String response = null, msg = null;
        try {
            callbackData = fileService.parseFile(file);
            System.out.println(callbackData.toString());
            response = fileService.sendData(callbackData, new JSONObject(clientInfo),callbackUrl);
            msg = String.format("File %s (%s) wurde erfolgreich bearbeitet. %s, %s", fileService.getName(), fileService.getHumanReadableSize(), callbackUrl, clientInfo);

        } catch (IOException e) {
            e.printStackTrace();
            msg = String.format("Daten könnten nicht versendete werden: %s", e.toString());

        }

        redirectAttributes.addFlashAttribute("message", msg);
        redirectAttributes.addFlashAttribute("response", response);

        return "redirect:/uploaded";
    }

}
