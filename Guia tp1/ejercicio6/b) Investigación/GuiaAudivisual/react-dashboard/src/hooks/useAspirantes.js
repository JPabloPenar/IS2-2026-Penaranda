import { useEffect, useState } from "react";
import { fecthAspirantes } from "../services/aspirantesServices";

export const useAspirantes = () =>{
    const [aspirantes, setAspirantes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(()=>{
        const obtenerAspirantes = async () =>{
            try {
                const data = await fecthAspirantes();
                setAspirantes(data);
            } catch (error) {
                setError(error.message)                
            }finally{
                setLoading(false);
            }
        }
        obtenerAspirantes();
    }, [])
    return {aspirantes,loading,error}
}