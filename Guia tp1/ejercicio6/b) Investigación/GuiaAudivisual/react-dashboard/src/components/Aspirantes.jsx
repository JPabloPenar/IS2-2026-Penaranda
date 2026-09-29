import AspirantesCard from "./AspirantesCard";
import { useAspirantes } from "../hooks/useAspirantes";

function Aspirantes() {
    const {aspirantes, loading, error} = useAspirantes();
    if(loading){
        return <p>Cragando aspirantes...</p>
    }
    if(error){
        return <p>Error: {error}</p>
    }
    return (
        <>
            <main className="content-wrap">
                <section className="content">
                    <h2>Aspirantes</h2>
                    <article className="person-boxes">
                        {
                            aspirantes.map((aspirante)=>(
                                <AspirantesCard 
                                    key={aspirante.id}
                                    aspirante={aspirante}
                                />    
                            ))
                        }
                        
                    </article>
                </section>
            </main>
        </>
    );
}
export default Aspirantes;