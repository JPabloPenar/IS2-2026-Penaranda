import foto1 from '../assets/img/foto1.jpg';
import foto2 from '../assets/img/foto2.jpg';
import foto3 from '../assets/img/foto3.jpg';
import foto4 from '../assets/img/foto4.jpg';
import foto5 from '../assets/img/foto5.jpg';
import foto6 from '../assets/img/foto6.jpg';

function Aspirantes() {
    return (
        <>
            <main className="content-wrap">
                <section className="content">
                    <h2>Aspirantes</h2>
                    <article className="person-boxes">
                        <div className="person-box shadow p-3 mb-5 bg-body-tertiary rounded">
                            <div className="box-avatar">
                                <img src={foto1} alt="Rocio"/>
                            </div>
                            <div className="box-bio">
                                <h2 className="bio-name">Rocio Carle</h2>
                                <p className="bio-position">Profesor</p>
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
                        <div className="person-box shadow p-3 mb-5 bg-body-tertiary rounded">
                            <div className="box-avatar">
                                <img src={foto2} alt="john-wick"/>
                            </div>
                            <div className="box-bio">
                                <h2 className="bio-name">Daniel Fuentes</h2>
                                <p className="bio-position">Técnico de sonido</p>
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
                        <div className="person-box shadow p-3 mb-5 bg-body-tertiary rounded">
                            <div className="box-avatar">
                                <img src={foto3} alt="Lee Chim"/>
                            </div>
                            <div className="box-bio">
                                <h2 className="bio-name">Lee Chim</h2>
                                <p className="bio-position">Linguista</p>
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
                        <div className="person-box shadow p-3 mb-5 bg-body-tertiary rounded">
                            <div className="box-avatar">
                                <img src={foto4} alt="Gloria"/>
                            </div>
                            <div className="box-bio">
                                <h2 className="bio-name">Gloria Medina</h2>
                                <p className="bio-position">Administrador</p>
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
                        <div className="person-box shadow p-3 mb-5 bg-body-tertiary rounded">
                            <div className="box-avatar">
                                <img src={foto5} alt="Victor"/>
                            </div>
                            <div className="box-bio">
                                <h2 className="bio-name">Victor Fuentes</h2>
                                <p className="bio-position">Computista</p>
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
                        <div className="person-box shadow p-3 mb-5 bg-body-tertiary rounded">
                            <div className="box-avatar">
                                <img src={foto6} alt="Luis"/>
                            </div>
                            <div className="box-bio">
                                <h2 className="bio-name">Luis Fuentes</h2>
                                <p className="bio-position">Economista</p>
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
                    </article>
                </section>
            </main>
        </>
    );
}
export default Aspirantes;