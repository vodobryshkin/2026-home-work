package company.vk.edu.distrib.compute.vodobryshkin;

public enum StatusCode {
    Ok(200),
    MethodNotAllowed(405),
    InternalServerError(503);

    private final int code;

    StatusCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
