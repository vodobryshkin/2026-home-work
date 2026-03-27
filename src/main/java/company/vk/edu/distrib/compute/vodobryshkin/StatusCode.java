package company.vk.edu.distrib.compute.vodobryshkin;

public enum StatusCode {
    Ok(200),
    Created(201),
    Accepted(202),
    BadRequest(400),
    NotFound(404),
    MethodNotAllowed(405),
    UnprocessableContent(422),
    InternalServerError(503);

    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
