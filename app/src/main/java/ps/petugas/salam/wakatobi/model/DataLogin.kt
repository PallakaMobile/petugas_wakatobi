package ps.petugas.salam.wakatobi.model

import android.os.Parcel
import android.os.Parcelable

/**
 **********************************************
 * Created by ukie on 3/20/17 with ♥
 * (>’_’)> email : ukie.tux@gmail.com
 * github : https://www.github.com/tuxkids <(’_’<)
 **********************************************
 * © 2017 | All Right Reserved
 */
class DataLogin {
    lateinit var diagnostic: Diagnostic
    lateinit var response: Response

    class Diagnostic {
        var status: Int = 0
        var description: String? = null
    }

    class Response : Parcelable {
        var id_user: String? = null
        var id_instansi: String? = null
        var nama: String? = null
        var uname: String? = null
        var mail: String? = null
        var foto: String? = null
        var instansi: String? = null
        var foto_instansi: String? = null
        var telp: String? = null
        var jabatan: String? = null
        var alamat: String? = null
        var poin: String? = null
        var aksi: String? = null
        var peringkat: String? = null
        var aktif: Int = 0
        var pass_encrypt: String? = null


        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeString(this.id_user)
            dest.writeString(this.id_instansi)
            dest.writeString(this.nama)
            dest.writeString(this.uname)
            dest.writeString(this.mail)
            dest.writeString(this.foto)
            dest.writeString(this.instansi)
            dest.writeString(this.foto_instansi)
            dest.writeString(this.telp)
            dest.writeString(this.jabatan)
            dest.writeString(this.alamat)
            dest.writeString(this.poin)
            dest.writeString(this.aksi)
            dest.writeString(this.peringkat)
            dest.writeInt(this.aktif)
            dest.writeString(this.pass_encrypt)
        }

        constructor()

        private constructor(`in`: Parcel) {
            this.id_user = `in`.readString()
            this.id_instansi = `in`.readString()
            this.nama = `in`.readString()
            this.uname = `in`.readString()
            this.mail = `in`.readString()
            this.foto = `in`.readString()
            this.instansi = `in`.readString()
            this.foto_instansi = `in`.readString()
            this.telp = `in`.readString()
            this.jabatan = `in`.readString()
            this.alamat = `in`.readString()
            this.poin = `in`.readString()
            this.aksi = `in`.readString()
            this.peringkat = `in`.readString()
            this.aktif = `in`.readInt()
            this.pass_encrypt = `in`.readString()
        }

        companion object {
            @JvmField
            var CREATOR: Parcelable.Creator<Response> = object : Parcelable.Creator<Response> {
                override fun createFromParcel(source: Parcel): Response {
                    return Response(source)
                }

                override fun newArray(size: Int): Array<Response> {
                    return newArray(size)
                }
            }
        }
    }
}