const API_URL = "https://randomuser.me/api/?results=9&nat=es";

export const fecthAspirantes = async () =>{
    try {
        const response = await fetch(API_URL);
        if(!response.ok){
            throw new Error('Error al obtener los aspirantes');
        }
        const data = await response.json();
        console.log(data);
        
        return data.results.map((user)=>({
            id : user.login.uuid,
            nombre : user.name.first,
            apellido : user.name.last,
            foto: user.picture.large,
            email: user.email
        }))
    } catch (error) {
        console.error('Ufff ha ocurrido un error:', error);
        throw error;
    }
}
