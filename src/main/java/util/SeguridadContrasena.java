
package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class SeguridadContrasena {

    private static final int ITERACIONES = 600_000;
    private static final int LONGITUD_SALT = 16;
    private static final int LONGITUD_HASH = 32;

    private SeguridadContrasena() {
    }

    public static String generarHash(String contrasena) {

        byte[] salt = new byte[LONGITUD_SALT];
        new SecureRandom().nextBytes(salt);

        byte[] hash = derivarClave(
                contrasena,
                salt,
                ITERACIONES
        );

        return "pbkdf2:"
                + ITERACIONES + ":"
                + Base64.getEncoder().encodeToString(salt)
                + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verificar(
            String contrasena,
            String valorAlmacenado
    ) {

        if (contrasena == null
                || valorAlmacenado == null) {
            return false;
        }

        if (!valorAlmacenado.startsWith("pbkdf2:")) {
            return false;
        }

        try {

            String[] partes = valorAlmacenado.split(":");

            if (partes.length != 4) {
                return false;
            }

            int iteraciones = Integer.parseInt(partes[1]);

            if (iteraciones < 1
                    || iteraciones > ITERACIONES) {
                return false;
            }

            byte[] salt = Base64.getDecoder()
                    .decode(partes[2]);

            byte[] hashGuardado = Base64.getDecoder()
                    .decode(partes[3]);

            if (salt.length != LONGITUD_SALT
                    || hashGuardado.length != LONGITUD_HASH) {
                return false;
            }

            byte[] hashIngresado = derivarClave(
                    contrasena,
                    salt,
                    iteraciones
            );

            return MessageDigest.isEqual(
                    hashGuardado,
                    hashIngresado
            );

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivarClave(
            String contrasena,
            byte[] salt,
            int iteraciones
    ) {

        PBEKeySpec especificacion = new PBEKeySpec(
                contrasena.toCharArray(),
                salt,
                iteraciones,
                LONGITUD_HASH * 8
        );

        try {

            SecretKeyFactory fabrica =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            return fabrica.generateSecret(
                    especificacion
            ).getEncoded();

        } catch (Exception e) {

            throw new IllegalStateException(
                    "No fue posible proteger la contraseña.",
                    e
            );

        } finally {
            especificacion.clearPassword();
        }
    }
}
