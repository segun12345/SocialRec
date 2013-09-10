package testinggrails

import grails.plugins.rest.client.RestBuilder
import org.springframework.dao.DataIntegrityViolationException
import util.ApiConsumer

import static util.ApiConsumer.postText

class StudentController {

    def addStudentService
    def removeStudentService

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [studentInstanceList: Student.list(params), studentInstanceTotal: Student.count()]
    }

    def create() {
        [studentInstance: new Student(params)]
    }

    def save() {
        def studentInstance = new Student(params)
        if (!studentInstance.save(flush: true)) {
            render(view: "create", model: [studentInstance: studentInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'student.label', default: 'Student'), studentInstance.id])
        redirect(action: "show", id: studentInstance.id)
    }

    def show(Long id) {
        def studentInstance = Student.get(id)
        if (!studentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'student.label', default: 'Student'), id])
            redirect(action: "list")
            return
        }

        [studentInstance: studentInstance]
    }

    def edit(Long id) {
        def studentInstance = Student.get(id)
        if (!studentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'student.label', default: 'Student'), id])
            redirect(action: "list")
            return
        }

        [studentInstance: studentInstance]
    }

    def update(Long id, Long version) {
        def studentInstance = Student.get(id)
        if (!studentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'student.label', default: 'Student'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (studentInstance.version > version) {
                studentInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'student.label', default: 'Student')] as Object[],
                          "Another user has updated this Student while you were editing")
                render(view: "edit", model: [studentInstance: studentInstance])
                return
            }
        }

        studentInstance.properties = params

        if (!studentInstance.save(flush: true)) {
            render(view: "edit", model: [studentInstance: studentInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'student.label', default: 'Student'), studentInstance.id])
        redirect(action: "show", id: studentInstance.id)
    }

    def delete(Long id) {
        def studentInstance = Student.get(id)
        if (!studentInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'student.label', default: 'Student'), id])
            redirect(action: "list")
            return
        }

        try {
            studentInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'student.label', default: 'Student'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'student.label', default: 'Student'), id])
            redirect(action: "show", id: id)
        }
    }

    def displayHeaderContent(){

       render ">>>"+removeStudentService.displayContent()

    }

    /* def url = "http://api.stage.metadata.bskyb.com"     //http://api.stage.metadata.bskyb.com
       def path = "/ajax/services/search/web"
       def query = [ v:'1.0', q: 'Calvin and Hobbes' ]

// Submit a request via GET
       def response = ApiConsumer.getText(url, path, query)

       println response */

// Submit a request via POST
    //def response = ApiConsumer.postText(url, path, query)

    // def rest = new RestBuilder()
    // def response = rest.get("http://api.stage.metadata.bskyb.com"){
    // contentType ""

//println response
//render(text: "<xml>some xml</xml>", contentType: "text/xml", encoding: "UTF-8")
    // // apikey "aa8ce3a2-9a30-4f29-a9c8-99cfb63dd07a"
    // apiversion "1.0"

}
