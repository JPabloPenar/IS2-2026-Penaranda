function AspirantesCard({ aspirante }) {
    const {nombre, apellido, foto,email} = aspirante;
    return (
        <div className="person-box shadow p-3 mb-5 bg-body-tertiary rounded">
            <div className="box-avatar">
                <img src={foto} alt={`${nombre} ${apellido}`} />
            </div>
            <div className="box-bio">
                <h2 className="bio-name">{nombre} {apellido}</h2>
                <p className="bio-position">{email}</p>
            </div>
            <div className="box-actions">
                <button>
                    <i className="bi bi-star"></i>
                </button>
                <button>
                    <i className="bi bi-chat"></i>
                </button>
                <button>
                    <i className="bi bi-envelope"></i>
                </button>
            </div>
        </div>
    )
}
export default AspirantesCard;
