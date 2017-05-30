package baxxo.matteo.smsapp;

/**
 * Created by Matteo on 15/04/2017.
 */

public class Messaggio {
    private String id = "";
    private String nome = "";
    private String numero = "";
    private String testo = "";
    private long data;
    private boolean inviato = false;
    private boolean da_inviare = true;

    public Messaggio(String id, String Nome, String Numero, String Testo, long data, boolean inviato) {
        this.id = id;
        this.nome = Nome;
        this.numero = Numero;
        this.testo = Testo;
        this.data = data;
        this.inviato = inviato;
    }

    public Messaggio(String id, String Nome, String Numero, String Testo, long data, boolean inviato, boolean da_inviare) {
        this.id = id;
        this.nome = Nome;
        this.numero = Numero;
        this.testo = Testo;
        this.data = data;
        this.inviato = inviato;
        this.da_inviare = da_inviare;
    }

    public Messaggio() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public long getData() {
        return data;
    }

    public void setData(long data) {
        this.data = data;
    }

    public boolean getInviato() {
        return inviato;
    }


    public void setInviato(boolean inviato) {
        this.inviato = inviato;
    }

    public boolean getDaInviato() {
        return da_inviare;
    }

    public void setDaInviato(boolean da_inviare) {
        this.da_inviare = da_inviare;
    }
}
